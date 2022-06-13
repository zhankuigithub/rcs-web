package com.longmaster.rcs.service;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * author zk
 * date 2021/2/24 17:55
 * description 拼接运营商群发消息体的service
 */
public interface IGroupBuildService {

    String buildMessage(JsonNode message);

    String buildSingleCard(JsonNode message);

    String buildManyCard(JsonNode message);

    String buildFile(JsonNode message);

}
