package com.longmaster.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.vo.Result;
import com.longmaster.core.http.HttpConfig;
import com.longmaster.core.http.HttpRequestBody;
import com.longmaster.core.http.IHttpRequest;
import com.longmaster.core.http.OkHttpRequestImpl;
import com.longmaster.platform.entity.Material;
import com.longmaster.platform.entity.MaterialAuditRecord;
import com.longmaster.platform.service.MaterialAuditRecordService;
import com.longmaster.platform.service.MaterialService;
import com.longmaster.platform.service.MessageBackService;
import com.longmaster.platform.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RefreshScope
public class MessageBackServiceImpl implements MessageBackService {

    @Value("${system.rcsMaap.server}")
    private String serverUrl;

    @Value("${sms.apiUrl}")
    private String apiUrl;

    @Value("${sms.appId}")
    private int appId;

    @Value("${sms.clientId}")
    private int clientId;

    @Value("${sms.clientKey}")
    private String clientKey;

    @Value("${sms.dynamicLink}")
    private String dynamicLink;

    @Resource
    private MaterialAuditRecordService materialAuditRecordService;

    @Resource
    private MaterialService materialService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RestTemplate balancedRestTemplate;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private static final int MSG_BACK_TEMPLATE_ID = 1355;
    private static final int MSG_BACK_2G_TEMPLATE_ID = 1369;

    private static final IHttpRequest okHttpRequest = new OkHttpRequestImpl(HttpConfig.defaultConfig());


    private String searchOriginalUrl(String auditUrl) {
        MaterialAuditRecord record = materialAuditRecordService.getOne(new LambdaQueryWrapper<MaterialAuditRecord>()
                .eq(MaterialAuditRecord::getMaterialUrl, auditUrl)
                .select(MaterialAuditRecord::getMaterialId));

        if (record != null) {
            Material material = materialService.getOne(new LambdaQueryWrapper<Material>().eq(Material::getId, record.getMaterialId()).select(Material::getSourceUrl));
            if (material != null) {
                return UrlUtil.buildMinIoURL(material.getSourceUrl());
            }
            return null;
        }
        return null;
    }


    @Override
    public boolean synchronousVirtualMapp(JsonNode original) {
        // 需要将消息体中运营商使用的媒体链接换成可以在h5上直接拉取的链接
        int type = original.get("type").asInt();
        switch (type) {
            case 2:
            case 3:
            case 5:
            case 6:
            case 7:
                JsonNode content = original.get("content");
                for (JsonNode node : content) {
                    if (node.has("thumb")) {
                        JsonNode thumb = node.get("thumb");
                        String url = thumb.get("url").asText();
                        ((ObjectNode) thumb).put("url", searchOriginalUrl(url));
                    }
                    if (node.has("file")) {
                        JsonNode file = node.get("file");
                        String url = file.get("url").asText();
                        ((ObjectNode) file).put("url", searchOriginalUrl(url));
                    }
                }
                break;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json"));
        HttpEntity httpEntity = new HttpEntity(original, headers);
        ResponseEntity<Result> result = balancedRestTemplate.exchange(serverUrl + "message/saveChatLog", HttpMethod.POST, httpEntity, Result.class);
        Result body = result.getBody();
        return (boolean) body.getData();
    }

    @Override
    public void sendH5Sms(String phone, String content, String chatBotId) {
        if (StrUtil.isEmpty(content) || "null".equals(content)) {
            log.info("content is null，phone：{}，chatBotId：{}", phone, chatBotId);
            return;
        }
        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("phone", phone);
        objectNode.put("chatBotId", chatBotId);

        log.info("send sms objectNode : {}", objectNode);

        Map smsInfo = new HashMap<>();
        smsInfo.put("client_id", clientId);
        smsInfo.put("app_id", appId);
        smsInfo.put("template_id", MSG_BACK_TEMPLATE_ID);
        smsInfo.put("mobile", phone);
        smsInfo.put("send_status", 1);

        Map templateArgs = new HashMap<>();
        templateArgs.put("p1", content);
        templateArgs.put("p2", createDynamicLink("https://rcs.langma.cn/chatbotH5/#/pages/chat/index?info=" + Base64.getEncoder().encodeToString(objectNode.toString().getBytes())));
        smsInfo.put("template_args", templateArgs);

        HttpRequestBody body = new HttpRequestBody();
        Map postData = new HashMap();
        try {
            postData.put("sms_info", sObjectMapper.writeValueAsString(smsInfo));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        body.setBodyForm(postData);
        body.setBodyType(HttpRequestBody.BODY_TYPE_FORM);

        try {
            Response post = okHttpRequest.POST(getReqUri(apiUrl + "/api/sendSms", appId, clientId, clientKey, phone, MSG_BACK_TEMPLATE_ID), body, null);
            log.info("send h5sms result : {}", post.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void send2gSms(String phone, String content) {
        if (StrUtil.isEmpty(content) || "null".equals(content)) {
            log.info("content is null，phone：{}", phone);
            return;
        }

        Map smsInfo = new HashMap<>();
        smsInfo.put("client_id", clientId);
        smsInfo.put("app_id", appId);
        smsInfo.put("template_id", MSG_BACK_2G_TEMPLATE_ID);
        smsInfo.put("mobile", phone);
        smsInfo.put("send_status", 1);

        Map templateArgs = new HashMap<>();
        templateArgs.put("smsContent", content);
        smsInfo.put("template_args", templateArgs);

        HttpRequestBody body = new HttpRequestBody();
        Map postData = new HashMap();
        try {
            postData.put("sms_info", sObjectMapper.writeValueAsString(smsInfo));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        body.setBodyForm(postData);
        body.setBodyType(HttpRequestBody.BODY_TYPE_FORM);

        try {
            Response post = okHttpRequest.POST(getReqUri(apiUrl + "/api/sendSms", appId, clientId, clientKey, phone, MSG_BACK_2G_TEMPLATE_ID), body, null);
            log.info("send 2gsms result : {}", post.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSign(int appId, int clientId, String clientKey, String mobile, int templateId) {
        String _str = "app_id=" + appId + "&client_id=" + clientId + "&client_key=" + clientKey + "&mobile=" + mobile + "&template_id=" + templateId;
        String sign = "";
        sign = DigestUtils.md5DigestAsHex(_str.getBytes());
        return sign;
    }

    private String getReqUri(String apiUrl, int appId, int clientId, String clientKey, String mobile, int templateId) {
        String sign = getSign(appId, clientId, clientKey, mobile, templateId);
        String uri = apiUrl + "?client_id=" + clientId + "&sign=" + sign;
        return uri;
    }

    private String createDynamicLink(String url) {
        try {
            log.info("createDynamicLink url is : {}", url);
            return restTemplate.getForObject(dynamicLink + URLEncoder.encode(url, "UTF-8"), String.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
