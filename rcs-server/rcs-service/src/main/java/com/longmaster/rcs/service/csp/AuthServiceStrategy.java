package com.longmaster.rcs.service.csp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.rcs.dto.csp.DeveloperDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * author zk
 * date 2021/10/13 16:17
 * description 电信联通，公用的
 */
@Slf4j
public abstract class AuthServiceStrategy extends CommonService implements AuthStrategy {

    @Resource
    private RestTemplate cspRestTemplate;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    private static final Long CARRIER_CUCC = 2L;
    private static final Long CARRIER_CTCC = 3L;

    /**
     * 获取接口访问凭证
     *
     * @return accessToken
     */
    @Override
    public String invokeAccessToken() {
        Long carrierId = carrierId();

        String url = baseUrl(carrierId).append("/accessToken").toString();
        log.info("[invokeAccessToken] invoke dian xin csp server url is : {}", url);

        ObjectNode objectNode = sObjectMapper.createObjectNode();

        if (carrierId.equals(CARRIER_CUCC)) {

            objectNode.put("cspId", env.getProperty("cuccLmtxCsp.cspId"));
            objectNode.put("accessKey", env.getProperty("cuccLmtxCsp.accessKey"));
        } else if (carrierId.equals(CARRIER_CTCC)) {

            objectNode.put("cspId", env.getProperty("ctccLmCsp.cspId"));
            objectNode.put("accessKey", env.getProperty("ctccLmCsp.accessKey"));
        }
        JsonNode result = cspRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(objectNode, baseHeaders(null, false, carrierId)), JsonNode.class).getBody();

        Assert.notNull(result, "CSP访问凭证获取失败");
        String accessToken = result.get("data").get(ACCESS_TOKEN_KEY).asText();

        String key = CSP_ACCESS_TOKEN + ":" + this.carrierId();
        redisTemplate.opsForValue().set(key, accessToken, CSP_TOKEN_EXPIRE_IN_S, TimeUnit.SECONDS);

        return accessToken;
    }


    /**
     * 开发者信息
     *
     * @param developer 开发者信息
     */
    @Override
    public JsonNode invokeUpdateDeveloper(DeveloperDTO developer) {
        String url = baseUrl(this.carrierId()).append("/developerConfiguration/updateDeveloper").toString();
        log.info("[invokeUpdateDeveloper] invoke dian xin csp server url is : {}", url);

        HttpHeaders headers = baseHeaders(carrierId());
        JsonNode result = cspRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(developer, headers), JsonNode.class).getBody();
        log.info("[invokeUpdateDeveloper] invoke execute result is {}", result);
        Assert.notNull(result, "开发者配置失败");
        return result;
    }

    @Override
    public String getAccessToken() {
        String key = CSP_ACCESS_TOKEN + ":" + this.carrierId();
        return redisTemplate.hasKey(key) ?
                redisTemplate.opsForValue().get(key) :
                invokeAccessToken();
    }
}
