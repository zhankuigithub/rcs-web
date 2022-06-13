package com.longmaster.platform.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface MessageBackService {

    /**
     * 同步数据到虚拟终端，数据格式为：t_log_send_message表的original
     *
     * @param original
     * @return
     */
    boolean synchronousVirtualMapp(JsonNode original);


    void sendH5Sms(String phone, String content, String chatBotId);


    void send2gSms(String phone, String content);
}
