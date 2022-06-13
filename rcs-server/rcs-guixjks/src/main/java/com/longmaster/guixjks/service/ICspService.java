package com.longmaster.guixjks.service;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * author zk
 * date 2021/3/15 17:06
 * description csp处理类
 */
public interface ICspService {

    /**
     * 获取访问token
     * @return
     */
    String accessToken();

    /**
     * 组建消息体向csp请求发消息
     *
     * @param jsonNode
     */
    void sendToCsp(JsonNode jsonNode);

    /**
     * 已知消息模板，向csp请求发消息
     *
     * @param chatBotId
     * @param userPhone
     * @param messageTmpId
     */
    void sendMessageByMessageTmpId(String chatBotId, String userPhone, boolean isMessageBack, Long messageTmpId);


    String getAuditMaterialUrl(String chatBotId, Long materialId, boolean isMessageBack);

    String getAuditMaterialUrlByOriginalUrl(String chatBotId, String originalUrl, boolean isMessageBack);

}
