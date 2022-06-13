package com.longmaster.rcs.service.channel.impl;

import cn.hutool.core.util.URLUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.threadpool.AsyncTaskThreadManager;
import com.longmaster.core.util.AuthorizationUtil;
import com.longmaster.core.util.TextUtil;
import com.longmaster.rcs.entity.ChatBotInfo;
import com.longmaster.rcs.service.IBuildJsonStrService;
import com.longmaster.rcs.service.ICspService;
import com.longmaster.rcs.service.IGroupBuildJsonService;
import com.longmaster.rcs.service.channel.ICuService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * author zk
 * date 2021/4/8 15:35
 * description 联通相关接口操作
 */
@Component
public class CuServiceImpl implements ICuService {

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ICspService cspService;

    @Resource
    private IBuildJsonStrService mBuildJsonStrService;

    @Resource
    private IGroupBuildJsonService mGroupBuildJsonService;

    @Value("${cucc.url}")
    private String cuccURL;

    @Value("${cucc.file}")
    private String cuccFile;

    private final Map<String, ReentrantLock> mReentrantLockMap = new ConcurrentHashMap<>();

    private static final String TOKEN = "/accessToken";
    private static final String MESSAGES = "/messages";
    private static final String DOWNLOAD = "/medias/download";
    private static final String FIND_INFO = "/find/chatBotInfo";
    private static final String UPD_MENU = "/update/chatBotInfo/menu";
    private static final String UPD_OPTIONALS = "/update/chatBotInfo/optionals";
    private static final String UPLOAD_FILE = "/medias/upload";
    private static final String DELETE_FILE = "/medias/delete";

    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN_";

    public StringBuilder baseUrl(String chatBotId) {
        return new StringBuilder(cuccURL).append(chatBotId);
    }

    public StringBuilder baseFileUrl(String chatBotId) {
        return new StringBuilder(cuccFile).append(chatBotId);
    }

    @Override
    public String getAccessToken(String chatBotId) throws InterruptedException {
        String key = ACCESS_TOKEN_KEY + chatBotId;

        String accessToken = stringRedisTemplate.opsForValue().get(key);

        if (!TextUtil.isEmpty(accessToken)) {
            return accessToken;
        }

        ChatBotInfo chatBorInfo = cspService.getChatBotInfo(chatBotId);
        if (chatBorInfo == null) {
            return null;
        }

        ReentrantLock keyLock = mReentrantLockMap.computeIfAbsent(key, k -> new ReentrantLock());
        if (keyLock.tryLock(2000, TimeUnit.MILLISECONDS)) {

            try {
                keyLock.lock();

                HttpHeaders headers = new HttpHeaders();
                headers.set("content-type", "application/json");
                headers.set("accept", "application/json");

                ObjectNode objectNode = sObjectMapper.createObjectNode();
                objectNode.put("appId", chatBorInfo.getAppId());
                objectNode.put("appKey", chatBorInfo.getAppKey());

                HttpEntity httpEntity = new HttpEntity(objectNode.toString(), headers);
                ResponseEntity<JsonNode> exchange = restTemplate.postForEntity(baseUrl(chatBotId).append(TOKEN).toString(), httpEntity, JsonNode.class);
                JsonNode body = exchange.getBody();
                accessToken = "accessToken " + body.get("accessToken").asText();

                stringRedisTemplate.opsForValue().set(key, accessToken, 60, TimeUnit.MINUTES);
            } finally {
                keyLock.unlock();
                mReentrantLockMap.remove(key);
            }
        }

        return accessToken;
    }

    @Override
    public String sendMessage(JsonNode request) {
        int type = request.get("type").asInt();
        String body = "";
        switch (type) {
            case 1:
            case 4:
                body = mBuildJsonStrService.buildMessage(request);
                break;
            case 2:
                body = mBuildJsonStrService.buildSingleCard(request);
                break;
            case 3:
                body = mBuildJsonStrService.buildManyCard(request);
                break;
            case 5:
            case 6:
            case 7:
                body = mBuildJsonStrService.buildFile(request);
                break;
        }
        return resolveMessage(request, body);
    }

    @Override
    public String sendGroupMessage(JsonNode request) {
        int type = request.get("type").asInt();
        String body = "";
        switch (type) {
            case 1:
            case 4:
                body = mGroupBuildJsonService.buildMessage(request);
                break;
            case 2:
                body = mGroupBuildJsonService.buildSingleCard(request);
                break;
            case 3:
                body = mGroupBuildJsonService.buildManyCard(request);
                break;
            case 5:
            case 6:
            case 7:
                body = mGroupBuildJsonService.buildFile(request);
                break;
        }
        return resolveMessage(request, body);
    }


    /**
     * @param request csp后台拼接的
     * @param body    运营商识别的
     * @return
     */
    private String resolveMessage(JsonNode request, String body) {
        String chatBotId = request.get("chatBotId").asText();

        String accessToken = "";
        try {
            accessToken = getAccessToken(chatBotId);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", accessToken);
        headers.set("content-type", "application/json");
        headers.set("accept", "application/json");
        headers.set("date", AuthorizationUtil.getGMTStr());
        HttpEntity httpEntity = new HttpEntity(body, headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(baseUrl(chatBotId).append(MESSAGES).toString(), httpEntity, JsonNode.class);
        JsonNode data = response.getBody();
        int errorCode = data.get("errorCode").asInt();

        if (errorCode == 0) {
            String messageId = data.get("messageId").asText();
            AsyncTaskThreadManager.getInstance().submit(() -> {
                try {
                    ObjectNode bodyJson = (ObjectNode) sObjectMapper.readTree(body);
                    bodyJson.put("messageId", messageId); // 联通的会返回新的msgid，因此需要替换，方便做统计

                    ObjectNode objectNode = sObjectMapper.createObjectNode();
                    objectNode.set("release", bodyJson);
                    objectNode.set("original", request);
                    cspService.saveCtSendMessage(objectNode);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
            return messageId;
        }
        return UUID.randomUUID().toString();
    }


    @Override
    public byte[] downloadFile(String chatBotId, String sourceUrl) {
        String accessToken = "";
        try {
            accessToken = getAccessToken(chatBotId);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return null;
        }

        HttpHeaders hh = new HttpHeaders();
        hh.set("authorization", accessToken);
        hh.set("url", sourceUrl);

        HttpEntity<Resource> httpEntity = new HttpEntity<>(hh);

        ResponseEntity<byte[]> result = restTemplate.exchange(baseFileUrl(chatBotId).append(DOWNLOAD).toString(), HttpMethod.GET, httpEntity, byte[].class);
        return result.getBody();
    }

    @Override
    public JsonNode getChatBotInformation(String chatBotId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("content-type", "application/json");
            headers.set("accept", "application/json");
            headers.set("authorization", getAccessToken(chatBotId));
            headers.set("date", AuthorizationUtil.getGMTStr());

            HttpEntity httpEntity = new HttpEntity(headers);

            ResponseEntity<String> response = restTemplate.exchange(baseUrl(chatBotId).append(FIND_INFO).toString(), HttpMethod.GET, httpEntity, String.class);
            return sObjectMapper.readTree(response.getBody());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JsonNode sendMenuAudit(String chatBotId, String menuJson) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("content-type", "application/json");
            headers.set("accept", "application/json");
            headers.set("authorization", getAccessToken(chatBotId));
            headers.set("date", AuthorizationUtil.getGMTStr());
            HttpEntity httpEntity = new HttpEntity(menuJson, headers);

            ResponseEntity<String> response = restTemplate.exchange(baseUrl(chatBotId).append(UPD_MENU).toString(), HttpMethod.POST, httpEntity, String.class);
            return sObjectMapper.readTree(response.getBody());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String updateOptionals(String chatBotId, String paramsJson) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("content-type", "application/json");
            headers.set("accept", "application/json");
            headers.set("authorization", getAccessToken(chatBotId));
            headers.set("date", AuthorizationUtil.getGMTStr());
            HttpEntity httpEntity = new HttpEntity(paramsJson, headers);

            ResponseEntity<String> response = restTemplate.exchange(baseUrl(chatBotId).append(UPD_OPTIONALS).toString(), HttpMethod.POST, httpEntity, String.class);
            return response.getBody();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public JsonNode uploadFile(String chatBotId, String sourceUrl, String uploadMode) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", getAccessToken(chatBotId));
            headers.set("uploadMode", uploadMode);
            headers.set("date", AuthorizationUtil.getGMTStr());

            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("file", new FileUrlResource(URLUtil.url(sourceUrl)));
            HttpEntity httpEntity = new HttpEntity<>(form, headers);
            ResponseEntity<String> response = restTemplate.exchange(baseFileUrl(chatBotId).append(UPLOAD_FILE).toString(), HttpMethod.POST, httpEntity, String.class);
            return sObjectMapper.readTree(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JsonNode deleteFile(String chatBotId, String sourceUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", getAccessToken(chatBotId));
            headers.set("url", sourceUrl);
            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(baseFileUrl(chatBotId).append(DELETE_FILE).toString(), HttpMethod.DELETE, httpEntity, String.class);
            return sObjectMapper.readTree(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
