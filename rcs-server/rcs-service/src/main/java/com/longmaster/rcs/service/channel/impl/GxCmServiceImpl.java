package com.longmaster.rcs.service.channel.impl;

import cn.hutool.core.util.URLUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.threadpool.AsyncTaskThreadManager;
import com.longmaster.core.util.AuthorizationUtil;
import com.longmaster.rcs.entity.ChatBotInfo;
import com.longmaster.rcs.service.IBuildXmlService;
import com.longmaster.rcs.service.ICspService;
import com.longmaster.rcs.service.IGroupBuildXmlService;
import com.longmaster.rcs.service.channel.IGxCmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * author zk
 * date 2021/4/8 15:48
 * description 广西移动消息接口
 */
@Slf4j
@Component
public class GxCmServiceImpl implements IGxCmService {

    @Value("${gxcmcc.url}")
    private String cmccURL;

    @Value("${gxcmcc.fileUrl}")
    private String cmccFileUrl;

    @Value("${gxcmcc.groupUrl}")
    private String cmccGroupURL;

    @Resource
    private ICspService cspService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private IBuildXmlService mBuildXmlService;

    @Resource
    private IGroupBuildXmlService mGroupBuildXmlService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private static final String REQUESTS = "/requests";

    @Override
    public String sendMessage(JsonNode request, String appId, String appKey) {
        int type = request.get("type").asInt();
        String body = "";
        switch (type) {
            case 1:
            case 4:
                body = mBuildXmlService.buildMessage(request);
                break;
            case 2:
                body = mBuildXmlService.buildSingleCard(request);
                break;
            case 3:
                body = mBuildXmlService.buildManyCard(request);
                break;
            case 5:
            case 6:
            case 7:
                body = mBuildXmlService.buildFile(request);
                break;
        }

        String requestURL = cmccURL + request.get("chatBotId").asText() + REQUESTS;

        HttpHeaders headers = new HttpHeaders();
        String date = AuthorizationUtil.getGMTStr();
        String auth = AuthorizationUtil.getAuthStr(date, appKey, appId);
        headers.set("Authorization", auth);
        headers.set("Content-Type", "application/xml");
        headers.set("Date", date);
        headers.set("Address", "+86" + request.get("userPhone").asText());
        HttpEntity httpEntity = new HttpEntity(body, headers);

        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("release", body); // 最终消息体
        objectNode.set("original", request);// 原始消息体，为方便统计使用
        return getMessageId(objectNode, requestURL, httpEntity);
    }

    @Override
    public String sendGroupMessage(JsonNode request, String appId, String appKey) {
        int type = request.get("type").asInt();
        String body = "";
        switch (type) {
            case 1:
            case 4:
                body = mGroupBuildXmlService.buildMessage(request);
                break;
            case 2:
                body = mGroupBuildXmlService.buildSingleCard(request);
                break;
            case 3:
                body = mGroupBuildXmlService.buildManyCard(request);
                break;
            case 5:
            case 6:
            case 7:
                body = mGroupBuildXmlService.buildFile(request);
                break;
        }
        String url = cmccGroupURL + request.get("chatBotId").asText() + REQUESTS;
        HttpHeaders headers = new HttpHeaders();
        String date = AuthorizationUtil.getGMTStr();
        String auth = AuthorizationUtil.getAuthStr(date, appKey, appId);
        headers.set("Authorization", auth);
        headers.set("Content-Type", "application/xml");
        headers.set("Date", date);
        headers.set("Address", "000");
        HttpEntity httpEntity = new HttpEntity(body, headers);

        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("release", body); // 最终消息体
        objectNode.set("original", request);// 原始消息体，为方便统计使用
        return getMessageId(objectNode, url, httpEntity);
    }

    @Override
    public byte[] downloadFile(String chatBotId, String appId, String appKey, String url) {
        HttpHeaders headers = new HttpHeaders();
        String date = AuthorizationUtil.getGMTStr();
        headers.set("Authorization", AuthorizationUtil.getAuthStr(date, appKey, appId));
        headers.set("Date", date);
        headers.set("X-3GPP-Intended-Identity", appId);
        headers.set("User-Agent", "SP/" + chatBotId);
        headers.set("Terminal-type", "Chatbot");

        HttpEntity<Resource> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, byte[].class);

        return response.getBody();
    }

    @Override
    public String uploadFile(String chatBotId, String sourceUrl) {
        ChatBotInfo chatBotInfo = cspService.getChatBotInfo(chatBotId);
        if (chatBotInfo == null) {
            return null;
        }

        String gmtStr = AuthorizationUtil.getGMTStr();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AuthorizationUtil.getAuthStr(gmtStr, chatBotInfo.getAppKey(), chatBotInfo.getAppId()));
        headers.set("date", gmtStr);
        headers.set("User-Agent", "SP/" + chatBotId);
        headers.set("Connection", "Keep-Alive");
        headers.set("X-3GPP-Intended-Identity", chatBotId);
        headers.set("Terminal-type", "Chatbot");

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("File", new FileUrlResource(URLUtil.url(sourceUrl)));
        HttpEntity httpEntity = new HttpEntity<>(form, headers);

        ResponseEntity<String> response = restTemplate.exchange(cmccFileUrl, HttpMethod.POST, httpEntity, String.class);
        HttpHeaders responseHeaders = response.getHeaders();
        List<String> list = responseHeaders.get("tid");
        log.info("gxcm uploadFile: {}", list);
        return list.get(0);
    }

    @Override
    public void deleteFile(String chatBotId, String tid) {
        ChatBotInfo chatBotInfo = cspService.getChatBotInfo(chatBotId);
        if (chatBotInfo == null) {
            return;
        }
        String gmtStr = AuthorizationUtil.getGMTStr();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + AuthorizationUtil.getAuthStr(gmtStr, chatBotInfo.getAppKey(), chatBotInfo.getAppId()));
        headers.set("date", gmtStr);
        headers.set("User-Agent", "SP/" + chatBotId);
        headers.set("Terminal-type", "Chatbot");
        headers.set("tid", tid);
        ResponseEntity<String> exchange = restTemplate.exchange(cmccFileUrl, HttpMethod.DELETE, new HttpEntity(headers), String.class);
        log.info("gxcm deleteFile: {} ，{}", exchange.getHeaders(), exchange.getStatusCode());
    }

    private String getMessageId(JsonNode body, String url, HttpEntity httpEntity) {
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, JsonNode.class);
        JsonNode node = response.getBody();
        if ("00000".equals(node.get("code").asText())) {
            String messageId = node.get("messageId").asText();
            AsyncTaskThreadManager.getInstance().submit(() -> cspService.saveCmSendMessage(messageId, body));  // 记录日志
            return messageId;
        }
        return null;
    }

}
