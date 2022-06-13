package com.longmaster.rcs.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.rcs.entity.ChatBotInfo;
import com.longmaster.rcs.service.ICspService;
import com.longmaster.rcs.service.IRedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
@RefreshScope
public class CspServiceServiceImpl implements ICspService {

    @Value("${rcs.csp-platform-url}")
    private String server;

    @Value("${system.info.appId}")
    private String appId;

    @Value("${system.info.appKey}")
    private String appKey;

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN_";

    @Resource
    private RestTemplate balancedRestTemplate;

    @Resource
    private IRedisService redisService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private HttpHeaders baseHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("appId", appId);
        headers.add("authorization", accessToken());
        return headers;
    }

    @Override
    public String accessToken() {
        String key = ACCESS_TOKEN + appId.toUpperCase();
        return redisService.unblockingGet(key, 7200, () -> { // 必须小于等于csp那边保存的时间
            ObjectNode node = sObjectMapper.createObjectNode();
            node.put("appId", appId);
            node.put("appKey", appKey);
            HttpEntity httpEntity = new HttpEntity(node);
            ResponseEntity<JsonNode> response = balancedRestTemplate.exchange(server + "/api/certificate/accessToken", HttpMethod.POST, httpEntity, JsonNode.class);
            JsonNode body = response.getBody();
            int code = body.get("code").asInt();
            if (code == 200) {
                return body.get("data").get("accessToken").asText();
            }
            return null;
        });
    }

    @Override
    public JsonNode saveLmSendMessage(JsonNode jsonNode) {
        HttpEntity httpEntity = new HttpEntity(jsonNode, baseHeader());
        ResponseEntity<JsonNode> exchange = balancedRestTemplate.exchange(server + "api/statistical/lm/saveSendMessage", HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public JsonNode saveCtSendMessage(JsonNode jsonNode) {
        HttpEntity httpEntity = new HttpEntity(jsonNode, baseHeader());
        ResponseEntity<JsonNode> exchange = balancedRestTemplate.exchange(server + "api/statistical/ctcc/saveSendMessage", HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public JsonNode saveCtSendMessageStatus(JsonNode jsonNode) {
        HttpEntity httpEntity = new HttpEntity(jsonNode, baseHeader());
        ResponseEntity<JsonNode> exchange = balancedRestTemplate.exchange(server + "api/statistical/ctcc/saveSendMessageStatus", HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public JsonNode saveCmSendMessage(String messageId, JsonNode jsonNode) {
        HttpEntity httpEntity = new HttpEntity(jsonNode, baseHeader());
        ResponseEntity<JsonNode> exchange = balancedRestTemplate.exchange(server + "api/statistical/gxCmcc/saveSendMessage/" + messageId, HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public JsonNode saveCmSendMessageStatus(String chatBotId, String xml) {
        HttpEntity httpEntity = new HttpEntity(xml, baseHeader());
        ResponseEntity<JsonNode> exchange = balancedRestTemplate.exchange(server + "api/statistical/gxCmcc/saveSendMessageStatus/" + chatBotId, HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public JsonNode auditCu(JsonNode jsonNode) {
        HttpEntity httpEntity = new HttpEntity(jsonNode, baseHeader());
        ResponseEntity<JsonNode> exchange = balancedRestTemplate.exchange(server + "api/audit/auditCUCC", HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public JsonNode auditCt(JsonNode jsonNode) {
        HttpEntity httpEntity = new HttpEntity(jsonNode, baseHeader());
        ResponseEntity<JsonNode> exchange = balancedRestTemplate.exchange(server + "api/audit/auditCTCC", HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public JsonNode auditCm(JsonNode jsonNode) {
        HttpEntity httpEntity = new HttpEntity(jsonNode, baseHeader());
        ResponseEntity<JsonNode> exchange = balancedRestTemplate.exchange(server + "api/audit/auditCMCC", HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public ChatBotInfo getChatBotInfo(String chatBotId) {
        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("chatBotId", chatBotId);
        ResponseEntity<JsonNode> post = balancedRestTemplate.postForEntity(server + "api/chatBot/getChatBotInfo", new HttpEntity(objectNode, baseHeader()), JsonNode.class);
        JsonNode body = post.getBody();

        if (body.get("code").asInt() != 200) {
            return null;
        }

        JsonNode jsonNode = body.get("data");
        if (jsonNode == null) {
            return null;
        }
        String appId = jsonNode.get("appId").asText();
        String appKey = jsonNode.get("appKey").asText();
        Long carrierId = jsonNode.get("carrierId").asLong();

        ChatBotInfo chatBorInfo = new ChatBotInfo();
        chatBorInfo.setAppId(appId);
        chatBorInfo.setChatBotId(chatBotId);
        chatBorInfo.setAppKey(appKey);
        chatBorInfo.setCarrierId(carrierId);
        return chatBorInfo;
    }

    @Override
    public JsonNode checkSensitiveWords(String content) {
        ObjectNode node = sObjectMapper.createObjectNode();
        node.put("content", content);
        HttpEntity httpEntity = new HttpEntity(node, baseHeader());
        ResponseEntity<JsonNode> exchange = balancedRestTemplate.exchange(server + "manage/sensitiveWords/check", HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

}
