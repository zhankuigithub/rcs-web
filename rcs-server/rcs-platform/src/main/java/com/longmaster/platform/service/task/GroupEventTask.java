package com.longmaster.platform.service.task;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.util.PhoneHelpUtil;
import com.longmaster.platform.entity.Carrier;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.LogTaskSendEvent;
import com.longmaster.platform.entity.PushEvent;
import com.longmaster.platform.mapper.ChatbotMapper;
import com.longmaster.platform.service.IPhoneNumberAppBlacklistService;
import com.longmaster.platform.service.LogTaskSendEventService;
import com.longmaster.platform.service.MessageService;
import com.longmaster.platform.service.PushEventService;
import com.longmaster.core.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GroupEventTask {

    @Resource
    private PushEventService pushEventService;

    @Resource
    private ChatbotMapper chatbotMapper;

    @Resource
    private MessageService messageService;

    @Resource
    private LogTaskSendEventService logTaskSendEventService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private IPhoneNumberAppBlacklistService phoneNumberAppBlacklistService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Scheduled(fixedDelay = 1000 * 5)
    public void pushEvent() throws JsonProcessingException {
        List<PushEvent> list = pushEventService.list(new LambdaQueryWrapper<PushEvent>().lt(PushEvent::getSendDt, LocalDateTime.now()).eq(PushEvent::getSendStatus, PushEvent.SEND_STATUS_INIT));
        if (list.size() > 0) {
            for (PushEvent pushEvent : list) {
                send(pushEvent);
                // 完成
                pushEvent.setSendStatus(PushEvent.SEND_STATUS_COMPLETE);
                pushEventService.updateById(pushEvent);
            }
        }
    }

    private void send(PushEvent event) throws JsonProcessingException {
        Long appId = event.getAppId();
        String carrierIds = event.getCarrierIds();
        Long templateId = event.getMessageTemplateId();
        String phoneNums = event.getPhoneNums();
        List<String> userPhones = Arrays.asList(phoneNums.split(","));
        String[] carrierIdsArray = carrierIds.split(",");

        Long carrierId = 0L;
        String chatBotId = "";
        for (String item : carrierIdsArray) {
            try {
                carrierId = Long.parseLong(item);

                Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>()
                        .eq(Chatbot::getCarrierId, carrierId)
                        .eq(Chatbot::getAppId, appId)
                        .select(Chatbot::getChatBotId, Chatbot::getStatus));

                if (chatbot == null) {
                    for (String userPhone : userPhones) {
                        UpdateWrapper<LogTaskSendEvent> wrapperRecord = new UpdateWrapper<>();
                        wrapperRecord
                                .eq("task_id", event.getId())
                                .eq("carrier_id", carrierId)
                                .eq("phone_num", userPhone)
                                .set("status", LogTaskSendEvent.STATUS_CHATBOT_IS_NOT_ENABLE);
                        logTaskSendEventService.update(null, wrapperRecord);
                    }
                    continue;
                }
                chatBotId = chatbot.getChatBotId();

                if (chatbot.getStatus() < 1) {
                    for (String userPhone : userPhones) {
                        UpdateWrapper<LogTaskSendEvent> wrapperRecord = new UpdateWrapper<>();
                        wrapperRecord
                                .eq("task_id", event.getId())
                                .eq("carrier_id", carrierId)
                                .eq("phone_num", userPhone)
                                .set("chatbot_id", chatBotId)
                                .set("status", LogTaskSendEvent.STATUS_CHATBOT_NEVER_AUDIT);
                        logTaskSendEventService.update(null, wrapperRecord);
                    }
                    continue;
                }

                ObjectNode data = messageService.buildMessage(templateId, appId, chatBotId, "", false);

                ArrayNode arrayNode = sObjectMapper.createArrayNode();
                // 过滤号码
                if (carrierId.equals(Carrier.LONGMASTER)) { // lm的不需要验证，直接发
                    userPhones.forEach(arrayNode::add);
                } else {
                    // 获取对应的号码
                    List<String> list = PhoneHelpUtil.filter(userPhones, carrierId.intValue());
                    list.forEach(arrayNode::add);
                }
                // 过滤黑名单
                for (int i = 0; i < arrayNode.size(); i++) {
                    boolean b = phoneNumberAppBlacklistService.isContainsBlackList(chatBotId, arrayNode.get(i).asText());
                    if (b) {
                        arrayNode.remove(i);
                    }
                }
                if (arrayNode.size() > 0) {
                    data.set("userPhones", arrayNode);
                    data.put("chatBotId", chatBotId);
                    data.put("carrierId", carrierId);

                    String messageId = rocketMQTemplate.sendAndReceive("RcsMessageSendTaskTopic:TaskTag", data, String.class);
                    log.info("RcsMessageSendTaskTopic:TaskTag，data：{}，result：{}", data, messageId);

                    if (!StrUtil.isEmpty(messageId)) {
                        if (carrierId == Carrier.LONGMASTER) {

                            Map<String, Object> map = sObjectMapper.readValue(messageId, Map.class);

                            for (JsonNode node : arrayNode) {
                                String phone = node.asText();
                                UpdateWrapper<LogTaskSendEvent> wrapperRecord = new UpdateWrapper<>();

                                wrapperRecord
                                        .eq("task_id", event.getId())
                                        .eq("carrier_id", carrierId)
                                        .eq("phone_num", phone)
                                        .set("chatbot_id", chatBotId);
                                if (map.containsKey(phone)) { // 包含，说明在线
                                    wrapperRecord.set("message_id", map.get(phone)).set("status", LogTaskSendEvent.STATUS_SUCCESS);
                                } else {
                                    wrapperRecord.set("status", LogTaskSendEvent.STATUS_USER_IS_NOT_ONLINE);
                                }
                                logTaskSendEventService.update(null, wrapperRecord);
                            }

                        } else {

                            // 这里只替换message_id 与 chatbot_id，status需要在下行消息回调中处理...
                            for (JsonNode node : arrayNode) {
                                String phone = node.asText();
                                UpdateWrapper<LogTaskSendEvent> wrapperRecord = new UpdateWrapper<>();
                                wrapperRecord
                                        .eq("task_id", event.getId())
                                        .eq("carrier_id", carrierId)
                                        .eq("phone_num", phone)
                                        .set("message_id", messageId)
                                        .set("chatbot_id", chatBotId);
                                logTaskSendEventService.update(null, wrapperRecord);
                            }
                        }
                    } else {

                        for (JsonNode phone : arrayNode) {
                            UpdateWrapper<LogTaskSendEvent> wrapperRecord = new UpdateWrapper<>();
                            wrapperRecord
                                    .eq("task_id", event.getId())
                                    .eq("carrier_id", carrierId)
                                    .eq("phone_num", phone.asText())
                                    .set("message_id", messageId)
                                    .set("chatbot_id", chatBotId)
                                    .set("status", LogTaskSendEvent.STATUS_404);
                            logTaskSendEventService.update(null, wrapperRecord);
                        }
                    }
                }

            } catch (Exception ex) {

                if (ex instanceof ServerException) {
                    String message = ex.getMessage();
                    JsonNode jsonNode = sObjectMapper.readTree(message);
                    int code = jsonNode.get("code").asInt();
                    // 捕获到异常的情况下，这里一般来自素材未审核
                    for (String userPhone : userPhones) {
                        UpdateWrapper<LogTaskSendEvent> wrapperRecord = new UpdateWrapper<>();
                        wrapperRecord
                                .eq("task_id", event.getId())
                                .eq("carrier_id", carrierId)
                                .eq("phone_num", userPhone)
                                .set("chatbot_id", chatBotId)
                                .set("status", code);
                        logTaskSendEventService.update(null, wrapperRecord);
                    }
                }

                if (ex instanceof MessagingException) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
