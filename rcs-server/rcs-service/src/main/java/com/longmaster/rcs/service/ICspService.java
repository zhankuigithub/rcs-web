package com.longmaster.rcs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.rcs.entity.ChatBotInfo;

public interface ICspService {

    /**
     * 获取token
     * @return
     */
    String accessToken();


    /**
     * 保存lm下行消息
     * @param jsonNode
     * @return
     */
    JsonNode saveLmSendMessage(JsonNode jsonNode);


    /**
     * 保存电信下行消息
     * @param jsonNode
     * @return
     */
    JsonNode saveCtSendMessage(JsonNode jsonNode);


    /**
     * 保存电信、联通的下行状态报告
     * @param jsonNode
     * @return
     */
    JsonNode saveCtSendMessageStatus(JsonNode jsonNode);


    /**
     * 保存移动的下行消息
     * @param messageId
     * @param jsonNode
     * @return
     */
    JsonNode saveCmSendMessage(String messageId, JsonNode jsonNode);


    /**
     * 保存移动的下行消息状态报告
     * @param chatBotId
     * @param xml
     * @return
     */
    JsonNode saveCmSendMessageStatus(String chatBotId, String xml);


    /**
     * 联通审核
     * @param jsonNode
     * @return
     */
    JsonNode auditCu(JsonNode jsonNode);


    /**
     * 电信审核
     * @param jsonNode
     * @return
     */
    JsonNode auditCt(JsonNode jsonNode);


    /**
     * 移送审核
     * @param jsonNode
     * @return
     */
    JsonNode auditCm(JsonNode jsonNode);


    /**
     * 获取chatbot
     * @param chatBotId
     * @return
     */
    ChatBotInfo getChatBotInfo(String chatBotId);


    /**
     * 检查敏感词
     * @param content
     * @return
     */
    JsonNode checkSensitiveWords(String content);

}
