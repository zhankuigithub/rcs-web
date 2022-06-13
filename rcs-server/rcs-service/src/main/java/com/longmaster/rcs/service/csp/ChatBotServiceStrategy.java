package com.longmaster.rcs.service.csp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.rcs.dto.csp.ChatBotDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * author zk
 * date 2021/10/13 16:17
 * description 电信联通，公用的
 */
@Slf4j
public abstract class ChatBotServiceStrategy extends CommonService implements ChatBotStrategy {

    @Resource
    private RestTemplate cspRestTemplate;


    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public JsonNode invokeUploadChatBotFile(MultipartFile file) {
        String url = baseUrl(carrierId()).append("/Chatbot/uploadChatbotFile").toString();

        HttpHeaders headers = baseHeaders(MediaType.parseMediaType("multipart/form-data"), carrierId());
        MultiValueMap form = new LinkedMultiValueMap<>();
        form.add("file", file.getResource());
        headers.set("uploadType", "0");
        JsonNode result = cspRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(form, headers), JsonNode.class).getBody();
        log.info("[invokeUploadChatBotFile] invoke execute result is {}", result);
        Assert.notNull(result, "上传机器人资料失败（NO RESULT）");
        return result;
    }

    @Override
    public JsonNode invokeCreateChatBot(ChatBotDTO wrapper) throws JsonProcessingException {
        String url = baseUrl(carrierId()).append("/Chatbot/addChatbot").toString();
        ObjectNode jsonNode = (ObjectNode) sObjectMapper.readTree(sObjectMapper.writeValueAsString(wrapper));

        jsonNode.put("chatbotId", wrapper.getChatBotId()); // 电信命名为chatbotId，做下兼容
        jsonNode.remove("chatBotId");

        jsonNode.put("cspId", wrapper.getCspCode()); // 命名修改成了cspId
        jsonNode.remove("cspCode");

        log.info("[invokeCreateChatBot] invoke dian xin csp server wrapper is : {}", jsonNode);

        JsonNode result = cspRestTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(jsonNode, baseHeaders(carrierId())),
                JsonNode.class
        ).getBody();

        log.info("[invokeCreateChatBot] invoke execute result is {}", result);
        Assert.notNull(result, "创建机器人失败（NO RESULT）");
        return result;
    }

    @Override
    public JsonNode invokeIsOnlineUpdate(String accessTagNo, Integer type) {
        Assert.notNull(type, "参数type 不允许为空~");
        Assert.isTrue(StringUtils.isNotBlank(accessTagNo), "参数 accessTagNo 不允许为空~");

        String url = baseUrl(carrierId()).append("/Chatbot/isOnlineUpdate").toString();
        log.info("[invokeIsOnlineUpdate] invoke dian xin csp server accessTagNo is : {}", accessTagNo);

        Map<String, Object> param = new HashMap<>();
        param.put("type", type);
        param.put("accessTagNo", accessTagNo);
        JsonNode result = cspRestTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(param, baseHeaders(carrierId())),
                JsonNode.class
        ).getBody();
        log.info("[invokeIsOnlineUpdate] invoke execute result is {}", result);
        Assert.notNull(result, "机器人状态变更失败（NO RESULT）");
        return result;
    }

    @Override
    public JsonNode invokeUpdateChatbot(ChatBotDTO wrapper) {
        String url = baseUrl(carrierId()).append("/Chatbot/updateChatbot").toString();
        log.info("[invokeUpdateChatbot] invoke dian xin csp server url is : {}", url);

        JsonNode result = cspRestTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(wrapper, baseHeaders(carrierId())),
                JsonNode.class
        ).getBody();
        log.info("[invokeUpdateChatbot] invoke execute result is {}", result);
        Assert.notNull(result, "创建机器人失败（NO RESULT）");
        return result;
    }

    @Override
    public JsonNode invokeDeleteChatbot(String accessTagNo) {
        String url = baseUrl(carrierId()).append("/Chatbot/deleteChatbot").toString();
        Map<String, Object> param = new HashMap<>();
        param.put("accessTagNo", accessTagNo);

        JsonNode result = cspRestTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(param, baseHeaders(carrierId())),
                JsonNode.class
        ).getBody();
        Assert.notNull(result, "删除机器人失败（NO RESULT）");
        return result;
    }
}
