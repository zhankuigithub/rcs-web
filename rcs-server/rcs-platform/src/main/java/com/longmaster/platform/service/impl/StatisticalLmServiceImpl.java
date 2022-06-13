package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.core.util.CommonUtil;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.LogSendMessage;
import com.longmaster.platform.entity.LogSendMessageStatus;
import com.longmaster.platform.service.ChatbotService;
import com.longmaster.platform.service.LogSendMessageService;
import com.longmaster.platform.service.LogSendMessageStatusService;
import com.longmaster.platform.service.StatisticalLmService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StatisticalLmServiceImpl implements StatisticalLmService {

    @Resource
    private LogSendMessageService logSendMessageService;

    @Resource
    private LogSendMessageStatusService logSendMessageStatusService;

    @Resource
    private ChatbotService chatbotService;

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public void saveSendMessage(String body) throws JsonProcessingException {
        JsonNode jsonNode = sObjectMapper.readTree(body);
        saveLogSendMessage(body, jsonNode.get("messageId").asText(), jsonNode.get("chatBotId").asText(), jsonNode.get("userPhone").asText());
    }

    @Override
    public void saveSendMessageStatus(String body) throws JsonProcessingException {

    }

    // 记录下行消息表
    private void saveLogSendMessage(String body, String messageId, String chatBotId, String phoneNum) {
        LogSendMessage logSendMessage = new LogSendMessage();
        logSendMessage.setMessageId(messageId);
        logSendMessage.setOriginal(body);
        logSendMessage.setContent(body);
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
        logSendMessageStatus.setPhoneNum(phoneNum);
        logSendMessageStatus.setChatBotId(chatBotId);
        logSendMessageStatusService.save(logSendMessageStatus);
    }
}
