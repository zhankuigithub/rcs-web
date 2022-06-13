package com.longmaster.platform.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.threadpool.AsyncTaskThreadManager;
import com.longmaster.core.util.CommonUtil;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.LogSendMessage;
import com.longmaster.platform.entity.LogSendMessageStatus;
import com.longmaster.platform.enums.DeliveryInfoEnum;
import com.longmaster.platform.mapper.LogTaskSendEventMapper;
import com.longmaster.platform.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StatisticalGxCmccServiceImpl implements StatisticalGxCmccService {

    @Resource
    private LogSendMessageStatusService logSendMessageStatusService;

    @Resource
    private LogSendMessageService logSendMessageService;

    @Resource
    private ChatbotService chatbotService;

    @Resource
    private LogTaskSendEventMapper logTaskSendEventMapper;

    @Resource
    private MessageBackService messageBackService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public void saveSendMessage(String body, String messageId) throws JsonProcessingException {
        JsonNode bodyJson = sObjectMapper.readTree(body);
        String release = bodyJson.get("release").asText();
        JsonNode original = bodyJson.get("original");

        JSONObject object = XML.toJSONObject(release);
        JsonNode jsonNode = sObjectMapper.readTree(object.toString());
        JsonNode outboundMessageRequest = jsonNode.get("msg:outboundMessageRequest");
        String chatBotId = outboundMessageRequest.get("senderAddress").asText();

        JsonNode destinationAddress = outboundMessageRequest.get("destinationAddress"); // 可能为数组或者字符串
        JsonNodeType nodeType = destinationAddress.getNodeType();
        if (nodeType == JsonNodeType.ARRAY) {
            destinationAddress.forEach(phoneNum -> saveLogSendMessage(release, original.toString(), messageId, chatBotId, phoneNum.asText()));
        } else if (nodeType == JsonNodeType.STRING) {
            saveLogSendMessage(release, original.toString(), messageId, chatBotId, destinationAddress.asText());
        }
    }


    @Override
    public void saveSendMessageStatus(String body, String chatBotId) throws JsonProcessingException {

        JSONObject object = XML.toJSONObject(body);
        JsonNode message = sObjectMapper.readTree(object.toString());
        JsonNode deliveryInfo = message.get("msg:deliveryInfoNotification").get("deliveryInfo");
        String phoneNum = deliveryInfo.get("address").asText();
        String messageId = deliveryInfo.get("messageId").asText();
        String deliveryStatus = deliveryInfo.get("deliveryStatus").asText();
        Integer status = DeliveryInfoEnum.getTypeByMode(deliveryStatus);
        saveLogSendMessageStatus(status, messageId, phoneNum, chatBotId);
        // 修改群发记录表里面的
        phoneNum = phoneNum.length() > 11 ? phoneNum.substring(phoneNum.length() - 11) : phoneNum;
        logTaskSendEventMapper.updateStatus(messageId, chatBotId, phoneNum, status);
    }


    // 记录下行消息表
    private void saveLogSendMessage(String release, String original, String messageId, String chatBotId, String phoneNum) {
        LogSendMessage logSendMessage = new LogSendMessage();
        logSendMessage.setMessageId(messageId);
        logSendMessage.setContent(release);
        logSendMessage.setOriginal(original);
        logSendMessage.setChatBotId(chatBotId);
        logSendMessage.setPhoneNum(CommonUtil.filterPhone(phoneNum));
        Chatbot chatbot = chatbotService.getOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, chatBotId).select(Chatbot::getAppId, Chatbot::getCarrierId));

        if (chatbot != null) {
            logSendMessage.setAppId(chatbot.getAppId());
            logSendMessage.setCarrierId(chatbot.getCarrierId());
            logSendMessageService.save(logSendMessage);
        }

    }

    // 记录状态表
    private void saveLogSendMessageStatus(Integer status, String messageId, String phoneNum, String chatBotId) {
        LogSendMessageStatus logSendMessageStatus = new LogSendMessageStatus();
        logSendMessageStatus.setStatus(status);
        logSendMessageStatus.setMessageId(messageId);
        logSendMessageStatus.setPhoneNum(CommonUtil.filterPhone(phoneNum));
        logSendMessageStatus.setChatBotId(chatBotId);
        Chatbot chatbot = chatbotService.getOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, chatBotId).select(Chatbot::getAppId, Chatbot::getCarrierId));

        if (chatbot != null) {
            logSendMessageStatus.setAppId(chatbot.getAppId());
            logSendMessageStatus.setCarrierId(chatbot.getCarrierId());
            logSendMessageStatusService.save(logSendMessageStatus);
        }
        phoneNum = phoneNum.length() > 11 ? phoneNum.substring(phoneNum.length() - 11) : phoneNum;

        String finalPhoneNum = phoneNum;
        AsyncTaskThreadManager.getInstance().submit(() -> {
            // 如果消息失败
            if (status == 2) {
                LogSendMessage one = logSendMessageService.getOne(new LambdaQueryWrapper<LogSendMessage>()
                        .eq(LogSendMessage::getMessageId, messageId)
                        .like(LogSendMessage::getPhoneNum, finalPhoneNum)
                        .select(LogSendMessage::getOriginal, LogSendMessage::getChatBotId));

                if (one != null) {
                    try {
                        ObjectNode node = (ObjectNode) sObjectMapper.readTree(one.getOriginal());
                        node.put("userPhone", finalPhoneNum); // 更新手机号
                        // 判断回落类型
                        int backType = node.get("backType").asInt();
                        switch (backType) {
                            case 1: // 回落h5
                                messageBackService.synchronousVirtualMapp(node);
                                if (node.has("smsContent")) {
                                    messageBackService.sendH5Sms(finalPhoneNum, node.get("smsContent").asText(), one.getChatBotId());
                                }
                                break;
                            case 2: // 回落2g短信
                                messageBackService.send2gSms(finalPhoneNum, node.get("smsContent").asText());
                                break;
                        }

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
