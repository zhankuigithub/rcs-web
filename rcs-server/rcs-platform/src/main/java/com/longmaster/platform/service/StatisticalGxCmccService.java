package com.longmaster.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface StatisticalGxCmccService {

    /**
     * 保存下行消息
     *
     * @param body
     */
    void saveSendMessage(String body, String messageId) throws JsonProcessingException;


    /**
     * 保存下行消息状态
     *
     * @param body
     */
    void saveSendMessageStatus(String body, String chatBotId) throws JsonProcessingException;


}
