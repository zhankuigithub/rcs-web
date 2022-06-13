package com.longmaster.rcs.controller.csp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.rcs.dto.csp.EnterpriseDTO;
import com.longmaster.rcs.service.csp.EnterpriseStrategy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/csp/ct/enterprise")
@Api(tags = "中国电信-客户")
public class CtEnterpriseController {

    @Resource
    private EnterpriseStrategy ctEnterpriseStrategy;

    @PostMapping("/uploadFile/{uploadType}")
    @ApiOperation("上传客户资料")
    public JsonNode uploadFile(@RequestParam MultipartFile file, @PathVariable Integer uploadType) {
        Assert.notNull(file, "文件流file不允许为空");
        Assert.notNull(uploadType, "文件类型不允许为空");
        return ctEnterpriseStrategy.invokeUploadFile(file, uploadType);
    }

    @PostMapping
    @ApiOperation("新增客户信息")
    public JsonNode addEnterprise(@RequestBody @Validated EnterpriseDTO wrapper) throws JsonProcessingException {
        return ctEnterpriseStrategy.invokeCreateCustomer(wrapper);
    }

    @DeleteMapping
    @ApiOperation("删除客户信息")
    public JsonNode deleteEnterprise(@RequestBody Map<String, Object> params) {
        Assert.isTrue(params.containsKey("cspEcNo"), "cspEcNo 不允许为空");
        return ctEnterpriseStrategy.invokeDeleteCustomer((String) params.get("cspEcNo"));
    }

    @PutMapping
    @ApiOperation("编辑客户信息")
    public JsonNode modifyEnterprise(@RequestBody @Validated(EnterpriseDTO.Update.class) EnterpriseDTO wrapper) throws JsonProcessingException {
        return ctEnterpriseStrategy.invokeUpdateCustomer(wrapper);
    }

    @GetMapping
    @ApiOperation("获取客户信息")
    public JsonNode selectCspCustomer(@RequestBody Map<String, Object> params) {
        Assert.isTrue(params.containsKey("cspEcNo"), "cspEcNo 不允许为空");
        return ctEnterpriseStrategy.invokeSelectCspCustomer((String) params.get("cspEcNo"));
    }
}
