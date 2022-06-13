package com.longmaster.rcs.service.channel.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.threadpool.AsyncTaskThreadManager;
import com.longmaster.rcs.service.ICspService;
import com.longmaster.rcs.service.channel.IRcsMaapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;

/**
 * author zk
 * date 2021/4/8 16:01
 * description rcs虚拟端 后端接口
 */
@Component
@RefreshScope
public class RcsMaapServiceImpl implements IRcsMaapService {

    @Value("${rcs.maap-url}")
    private String rcsMaapURL;

    @Resource
    private ICspService cspService;

    @Resource
    private RestTemplate balancedRestTemplate;

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public void sendMessage(JsonNode jsonNode, String chatBotId) {
        HttpEntity httpEntity = new HttpEntity(jsonNode);
        ResponseEntity<JsonNode> response = balancedRestTemplate.exchange(rcsMaapURL + "message/sendMessage/" + chatBotId, HttpMethod.POST, httpEntity, JsonNode.class);
        JsonNode body = response.getBody();
        if (body.get("code").asInt() == 200) {
            AsyncTaskThreadManager.getInstance().submit(() -> cspService.saveLmSendMessage(jsonNode));
        }
    }

    @Override
    public String sendGroupMessage(JsonNode jsonNode, String chatBotId) {
        HttpEntity httpEntity = new HttpEntity(jsonNode);
        ResponseEntity<JsonNode> response = balancedRestTemplate.exchange(rcsMaapURL + "message/sendGroupMessage/" + chatBotId, HttpMethod.POST, httpEntity, JsonNode.class);
        JsonNode body = response.getBody();
        try {
            JsonNode data = body.get("data");
            String value = sObjectMapper.writeValueAsString(data); // 不可直接转text

            AsyncTaskThreadManager.getInstance().submit(() -> {
                Map<String, Object> map = null;
                try {
                    map = sObjectMapper.readValue(value, Map.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                Iterator iterator = map.keySet().iterator();
                if (iterator.hasNext()) {
                    ObjectNode message = (ObjectNode) jsonNode;
                    String phone = (String) iterator.next();
                    String messageId = (String) map.get(phone);
                    message.put("messageId", messageId);
                    message.put("userPhone", phone);
                    message.remove("userPhones");
                    cspService.saveLmSendMessage(message);
                }
            });
            return value;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
