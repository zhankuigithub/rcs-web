package com.longmaster.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface StatisticalLmService {

    /**
     * 保存下行消息
     *
     * @param body
     */
    void saveSendMessage(String body) throws JsonProcessingException;

    /**
     * 保存下行消息状态
     *
     * @param body
     */
    void saveSendMessageStatus(String body) throws JsonProcessingException;

}
