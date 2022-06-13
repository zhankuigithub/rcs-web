package com.longmaster.rcs.service.channel;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * author zk
 * date 2021/4/8 16:01
 * description rcs虚拟端 后端接口
 */
public interface IRcsMaapService {

    /**
     * 单发
     * @param jsonNode
     * @param chatBotId
     */
    void sendMessage(JsonNode jsonNode, String chatBotId);

    /**
     * 群发
     * @param jsonNode
     * @param chatBotId
     * @return
     */
    String sendGroupMessage(JsonNode jsonNode, String chatBotId);
}
