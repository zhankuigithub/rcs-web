package com.longmaster.platform.controller.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.entity.AccessCertificate;
import com.longmaster.platform.service.AccessCertificateService;
import com.longmaster.platform.service.RedisService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("api/certificate")
@Api(value = "AccessCertificateController", tags = "访问凭证")
public class AccessCertificateController {

    @Resource
    private AccessCertificateService accessCertificateService;

    @Resource
    private RedisService redisService;

    private static final long HOURS = 3600;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private static final Long MAX_COUNT = 2L; // 每小时最大上限

    @PostMapping("accessToken")
    public Result accessToken(@RequestBody JsonNode request) {
        Assert.isTrue(request.has("appId"), new ServerException("缺少参数appId"));
        Assert.isTrue(request.has("appKey"), new ServerException("缺少参数appKey"));

        String appId = request.get("appId").asText();
        String appKey = request.get("appKey").asText();

        String countKey = "COUNT_" + appId;
        Long increment = redisService.increment(countKey);
        if (increment.intValue() == 1) { // 新加入的key，设置1小时缓存
            redisService.expire(countKey, HOURS);
        }
        Assert.isFalse(increment > MAX_COUNT, new ServerException("当前一小时已超过%s次", MAX_COUNT));

        long count = accessCertificateService.count(new LambdaQueryWrapper<AccessCertificate>()
                .eq(AccessCertificate::getAppId, appId)
                .eq(AccessCertificate::getSecret, appKey));

        Assert.isTrue(count > 0, new ServerException("错误的appId或appKey"));

        String accessToken = "accessToken " + Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());

        redisService.set(accessToken, appId, HOURS * 2 + 10);// 多预留10s

        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("accessToken", accessToken);
        objectNode.put("expires", HOURS * 2);
        return Result.SUCCESS(objectNode);
    }
}
