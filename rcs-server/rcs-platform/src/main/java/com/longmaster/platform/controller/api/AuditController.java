package com.longmaster.platform.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.service.AuditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * author zk
 * date 2021/2/27 9:23
 * description 运营商回调审核入口
 */
@RestController
@RequestMapping("api/audit")
@Api(value = "AuditController", tags = "运营商回调审核")
public class AuditController {

    @Resource
    private AuditService auditService;

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @PostMapping("auditCMCC")
    @ApiOperation(value = "移动审核", notes = "移动的回调审核（写素材审核记录）")
    public Result auditCMCC(@RequestBody String json) throws JsonProcessingException {
        return Result.SUCCESS(auditService.auditCMCC(sObjectMapper.readTree(json)));
    }

    @PostMapping("auditCTCC")
    @ApiOperation(value = "电信审核", notes = "电信的回调审核（更新审核记录）")
    public Result auditCTCC(@RequestBody String json) throws JsonProcessingException {
        return Result.SUCCESS(auditService.auditCTCC(sObjectMapper.readTree(json)));
    }

    @PostMapping("auditCUCC")
    @ApiOperation(value = "联通审核", notes = "联通的回调审核（更新审核记录）")
    public Result auditCUCC(@RequestBody String json) throws JsonProcessingException {
        return Result.SUCCESS(auditService.auditCUCC(sObjectMapper.readTree(json))); // 和电信逻辑一致
    }

}
