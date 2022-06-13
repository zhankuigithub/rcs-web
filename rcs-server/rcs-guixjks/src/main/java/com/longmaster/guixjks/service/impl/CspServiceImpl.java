package com.longmaster.guixjks.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.guixjks.Constants;
import com.longmaster.guixjks.service.ICspService;
import com.longmaster.guixjks.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * author zk
 * date 2021/3/15 17:06
 * description csp处理类
 */
@Component
@RefreshScope
public class CspServiceImpl implements ICspService {

    @Value("${csp.apiUrl}")
    private String server;

    @Value("${system.info.appId}")
    private String appId;

    @Value("${system.info.appKey}")
    private String appKey;

    @Resource
    private RestTemplate balancedRestTemplate;

    @Resource
    private RedisService redisService;

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN_";

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
        return redisService.unblockingGet(key, 7200, () -> {
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
    public void sendToCsp(JsonNode jsonNode) {
        HttpEntity httpEntity = new HttpEntity(jsonNode, baseHeader());
        balancedRestTemplate.exchange(server + Constants.Csp.MESSAGE, HttpMethod.POST, httpEntity, String.class);
    }

    @Override
    public void sendMessageByMessageTmpId(String chatBotId, String userPhone, boolean isMessageBack, Long messageTmpId) {
        ObjectNode objectNode = sObjectMapper.createObjectNode();

        objectNode.put("chatBotId", chatBotId);
        objectNode.put("userPhone", userPhone);
        objectNode.put("messageTmpId", messageTmpId);
        objectNode.put("isMessageBack", isMessageBack);

        HttpHeaders headers = baseHeader();
        headers.set("Content-Type", "application/json");

        HttpEntity httpEntity = new HttpEntity(objectNode.toString(), headers);
        balancedRestTemplate.exchange(server + Constants.Csp.MESSAGE_MSG_TMP, HttpMethod.POST, httpEntity, String.class);
    }

    @Override
    public String getAuditMaterialUrl(String chatBotId, Long materialId, boolean isMessageBack) {
        HttpEntity httpEntity = new HttpEntity(baseHeader());
        JsonNode result = balancedRestTemplate.exchange(
                server + Constants.Csp.GET_MATERIAL_URL + "?chatBotId=" + chatBotId + "&materialId=" + materialId + "&isMessageBack=" + isMessageBack,
                HttpMethod.GET,
                httpEntity,
                JsonNode.class).getBody();
        JsonNode data = result.get("data");
        if (data != null) {
            return data.get("materialUrl").asText();
        }
        return null;
    }

    @Override
    public String getAuditMaterialUrlByOriginalUrl(String chatBotId, String originalUrl, boolean isMessageBack) {
        HttpEntity httpEntity = new HttpEntity(baseHeader());
        JsonNode result = balancedRestTemplate.exchange(
                server + Constants.Csp.GET_MATERIAL_URL_BY_ORIGINAL + "?chatBotId=" + chatBotId + "&originalUrl=" + originalUrl + "&isMessageBack=" + isMessageBack,
                HttpMethod.GET,
                httpEntity,
                JsonNode.class).getBody();
        int code = result.get("code").asInt();
        if (code == 200) {
            JsonNode data = result.get("data");
            if (data != null) {
                return data.get("materialUrl").asText();
            }
        }
        return null;
    }

}
