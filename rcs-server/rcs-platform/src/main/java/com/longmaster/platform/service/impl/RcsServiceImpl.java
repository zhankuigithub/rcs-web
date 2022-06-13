package com.longmaster.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.platform.dto.csp.ChatBotWithCtDTO;
import com.longmaster.platform.dto.csp.DeveloperDTO;
import com.longmaster.platform.dto.csp.EnterpriseAuthWithCtDTO;
import com.longmaster.platform.enums.UploadType;
import com.longmaster.platform.service.RcsService;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RefreshScope
public class RcsServiceImpl implements RcsService {

    @Value("${system.rcsServer.server}")
    private String serverUrl;

    @Resource
    private RestTemplate balancedRestTemplate;

    @Override
    public String ctUploadChatBotInfoFile(String filePath) {
        Assert.isTrue(StrUtil.isNotBlank(filePath), new ServiceException("机器人资料不允许为空"));
        String url = serverUrl + "/csp/chatbot/ct/uploadFile";

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new FileUrlResource(URLUtil.url(filePath)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("multipart/form-data"));
        JsonNode result = balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(form, headers), JsonNode.class).getBody();
        Assert.isTrue(result != null && result.get("code").asInt() == 0, new ServiceException("资料上传失败(%s)~", result.get("code")));
        return result.get("code").asInt() == 0 ? result.get("data").get("filePath").asText() : null;
    }

    @Override
    public JsonNode ctCreateChatBot(ChatBotWithCtDTO chatBot) {
        String url = serverUrl + "/csp/chatbot/ct";
        return balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(chatBot), JsonNode.class).getBody();
    }

    @Override
    public JsonNode ctUpdateChatBot(ChatBotWithCtDTO chatBot) {
        String url = serverUrl + "/csp/chatbot/ct";
        return balancedRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(chatBot), JsonNode.class).getBody();
    }

    @Override
    public JsonNode ctOnlineChatBot(Map<String, Object> param) {
        String url = serverUrl + "/csp/chatbot/ct/online";
        return balancedRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(param), JsonNode.class).getBody();
    }

    @Override
    public JsonNode ctDeveloper(DeveloperDTO developer) {
        String url = serverUrl + "/csp/chatbot/ct/developer";
        return balancedRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(developer), JsonNode.class).getBody();
    }


    @Override
    public JsonNode ctSubmitCustomerAudit(HttpMethod method, EnterpriseAuthWithCtDTO enterprise) {
        String url = serverUrl + "/csp/ct/enterprise";
        JsonNode result = balancedRestTemplate.exchange(url, method, new HttpEntity<>(enterprise), JsonNode.class).getBody();
        log.info("[ctSubmitCustomerAudit] submit customer, result is {}", result);
        return result;
    }

    @Override
    public String ctUploadCustomerFile(String fileUrl, UploadType uploadType) {
        String url = serverUrl + "/csp/ct/enterprise/uploadFile/" + uploadType.getType();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("multipart/form-data"));
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new FileUrlResource(URLUtil.url(fileUrl)));
        JsonNode body = balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(form, headers), JsonNode.class).getBody();

        Assert.notNull(body, new ServiceException("客户资料上传失败(NO RESULT)~"));
        Assert.isTrue(body.get("code").asInt() == 0, new ServiceException("资料上传失败(%s)~", body.get("code")));
        return body.get("data") != null ? body.get("data").get("url").asText() : "";
    }

    @Override
    public JsonNode ctChatBotInfo(String chatBotId) {
        String url = serverUrl + "/csp/chatbot/ct/info?chatBotId=" + chatBotId;
        return balancedRestTemplate.getForEntity(url, JsonNode.class).getBody();
    }

    @Override
    public JsonNode ctSendMenuAudit(String chatBotId, String menuJson) {
        String url = serverUrl + "/csp/chatbot/ct/sendMenu";
        Map map = new HashMap();
        map.put("chatBotId", chatBotId);
        map.put("menuJson", menuJson);
        return balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(map), JsonNode.class).getBody();
    }

    @Override
    public JsonNode ctUploadChatBotMaterial(String chatBotId, String sourceUrl, String uploadMode) {
        String url = serverUrl + "/csp/chatbot/ct/uploadMaterial";
        Map map = new HashMap();
        map.put("chatBotId", chatBotId);
        map.put("sourceUrl", sourceUrl);
        map.put("uploadMode", uploadMode);
        return balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(map), JsonNode.class).getBody();
    }

    @Override
    public JsonNode ctDeleteChatBotMaterial(String chatBotId, String sourceUrl) {
        String url = serverUrl + "/csp/chatbot/ct/deleteMaterial";
        Map map = new HashMap();
        map.put("chatBotId", chatBotId);
        map.put("sourceUrl", sourceUrl);
        return balancedRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(map), JsonNode.class).getBody();
    }

    @Override
    public String cuUploadChatBotInfoFile(String filePath) {
        Assert.isTrue(StrUtil.isNotBlank(filePath), new ServiceException("机器人资料不允许为空"));
        String url = serverUrl + "/csp/chatbot/cu/uploadFile";

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new FileUrlResource(URLUtil.url(filePath)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("multipart/form-data"));
        JsonNode result = balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(form, headers), JsonNode.class).getBody();
        Assert.isTrue(result != null && result.get("code").asInt() == 0, new ServiceException("服务调用失败"));
        return result.get("code").asInt() == 0 ? result.get("data").get("filePath").asText() : null;
    }

    @Override
    public JsonNode cuCreateChatBot(ChatBotWithCtDTO chatBot) {
        String url = serverUrl + "/csp/chatbot/cu";
        return balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(chatBot), JsonNode.class).getBody();
    }

    @Override
    public JsonNode cuUpdateChatBot(ChatBotWithCtDTO chatBot) {
        String url = serverUrl + "/csp/chatbot/cu";
        return balancedRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(chatBot), JsonNode.class).getBody();
    }

    @Override
    public JsonNode cuOnlineChatBot(Map<String, Object> param) {
        String url = serverUrl + "/csp/chatbot/cu/online";
        return balancedRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(param), JsonNode.class).getBody();
    }

    @Override
    public JsonNode cuDeveloper(DeveloperDTO developer) {
        String url = serverUrl + "/csp/chatbot/cu/developer";
        return balancedRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(developer), JsonNode.class).getBody();
    }

    @Override
    public JsonNode cuSubmitCustomerAudit(HttpMethod method, EnterpriseAuthWithCtDTO enterprise) {
        String url = serverUrl + "/csp/cu/enterprise";
        JsonNode result = balancedRestTemplate.exchange(url, method, new HttpEntity<>(enterprise), JsonNode.class).getBody();
        log.info("[ctSubmitCustomerAudit] submit customer, result is {}", result);
        return result;
    }

    @Override
    public String cuUploadCustomerFile(String fileUrl, UploadType uploadType) {
        String url = serverUrl + "/csp/cu/enterprise/uploadFile/" + uploadType.getType();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("multipart/form-data"));
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new FileUrlResource(URLUtil.url(fileUrl)));
        JsonNode body = balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(form, headers), JsonNode.class).getBody();

        Assert.notNull(body, new ServiceException("客户资料上传失败(NO RESULT)~"));
        Assert.isTrue(body.get("code").asInt() == 0, new ServiceException("资料上传失败(%s)~", body.get("code")));
        return body.get("data") != null ? body.get("data").get("url").asText() : "";
    }

    @Override
    public JsonNode cuChatBotInfo(String chatBotId) {
        String url = serverUrl + "/csp/chatbot/cu/info?chatBotId=" + chatBotId;
        return balancedRestTemplate.getForEntity(url, JsonNode.class).getBody();
    }

    @Override
    public JsonNode cuSendMenuAudit(String chatBotId, String menuJson) {
        String url = serverUrl + "/csp/chatbot/cu/sendMenu";
        Map map = new HashMap();
        map.put("chatBotId", chatBotId);
        map.put("menuJson", menuJson);
        return balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(map), JsonNode.class).getBody();
    }

    @Override
    public JsonNode cuUploadChatBotMaterial(String chatBotId, String sourceUrl, String uploadMode) {
        String url = serverUrl + "/csp/chatbot/cu/uploadMaterial";
        Map map = new HashMap();
        map.put("chatBotId", chatBotId);
        map.put("sourceUrl", sourceUrl);
        map.put("uploadMode", uploadMode);
        return balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(map), JsonNode.class).getBody();
    }

    @Override
    public JsonNode cuDeleteChatBotMaterial(String chatBotId, String sourceUrl) {
        String url = serverUrl + "/csp/chatbot/cu/deleteMaterial";
        Map map = new HashMap();
        map.put("chatBotId", chatBotId);
        map.put("sourceUrl", sourceUrl);
        return balancedRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(map), JsonNode.class).getBody();
    }

    @Override
    public String cmUploadChatBotMaterial(String chatBotId, String sourceUrl) {
        String url = serverUrl + "/csp/chatbot/cm/uploadMaterial";
        Map map = new HashMap();
        map.put("chatBotId", chatBotId);
        map.put("sourceUrl", sourceUrl);
        return balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(map), String.class).getBody();
    }

    @Override
    public void cmDeleteChatBotMaterial(String chatBotId, String tid) {
        String url = serverUrl + "/csp/chatbot/cm/deleteMaterial";
        Map map = new HashMap();
        map.put("chatBotId", chatBotId);
        map.put("tid", tid);
        balancedRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(map), JsonNode.class).getBody();
    }

    @Override
    public String gxCmUploadChatBotMaterial(String chatBotId, String sourceUrl) {
        String url = serverUrl + "/csp/chatbot/gxCm/uploadMaterial";
        Map map = new HashMap();
        map.put("chatBotId", chatBotId);
        map.put("sourceUrl", sourceUrl);
        return balancedRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(map), String.class).getBody();
    }

    @Override
    public void gxCmDeleteChatBotMaterial(String chatBotId, String tid) {
        String url = serverUrl + "/csp/chatbot/gxCm/deleteMaterial";
        Map map = new HashMap();
        map.put("chatBotId", chatBotId);
        map.put("tid", tid);
        balancedRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(map), JsonNode.class).getBody();
    }

}
