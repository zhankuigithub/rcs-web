package com.longmaster.platform.service.impl;

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
public class StatisticalCtccServiceImpl implements StatisticalCtccService {

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
    public void saveSendMessage(String body) throws JsonProcessingException {
        JsonNode bodyJson = sObjectMapper.readTree(body);
        JsonNode release = bodyJson.get("release");
        JsonNode original = bodyJson.get("original");

        JsonNode destinationAddress = release.get("destinationAddress");
        if (destinationAddress.getNodeType() == JsonNodeType.ARRAY) {

            String messageId = release.get("messageId").asText();
            String chatBotId = release.get("senderAddress").asText();

            destinationAddress.forEach(node -> {
                saveLogSendMessage(release.toString(), original.toString(), messageId, chatBotId, node.asText());
            });
        }
    }


    @Override
    public void saveSendMessageStatus(String body) throws JsonProcessingException {

        JsonNode message = sObjectMapper.readTree(body);
        JsonNode deliveryInfo = message.get("deliveryInfoList").get(0); // 原始为array类型
        String phoneNum = deliveryInfo.get("senderAddress").asText();
        String chatBotId = deliveryInfo.get("destinationAddress").asText();
        String messageId = deliveryInfo.get("messageId").asText();
        Integer status = DeliveryInfoEnum.getTypeByMode(deliveryInfo.get("status").asText());
        saveLogSendMessageStatus(status, messageId, phoneNum, chatBotId);

        // 修改群发记录表里面的
        phoneNum = phoneNum.length() > 11 ? phoneNum.substring(phoneNum.length() - 11) : phoneNum;
        logTaskSendEventMapper.updateStatus(messageId, chatBotId, phoneNum, status);

        String finalPhoneNum = phoneNum;
        AsyncTaskThreadManager.getInstance().submit(() -> {
            // 如果消息失败
            if (status == 2) {
                LogSendMessage one = logSendMessageService.
                        getOne(new LambdaQueryWrapper<LogSendMessage>()
                                .eq(LogSendMessage::getMessageId, messageId)
                                .like(LogSendMessage::getPhoneNum, finalPhoneNum)
                                .select(LogSendMessage::getOriginal, LogSendMessage::getChatBotId, LogSendMessage::getPhoneNum));
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

    }

}
