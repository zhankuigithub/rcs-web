package com.longmaster.platform.controller.manage;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.customer.CustomerAuditRecordDTO;
import com.longmaster.platform.service.CustomerAuditRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 客户审核记录表 前端控制器
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Slf4j
@RestController
@RequestMapping("/manage/customerAuditRecord")
@Api(value = "CustomerAuditRecordController", tags = "客户审核信息")
public class CustomerAuditRecordController {

    @Resource
    private CustomerAuditRecordService auditRecordService;

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "分页查询客户审核信息")
    public Result pageCustomer(@RequestBody PageParam<CustomerAuditRecordDTO> pageParam, HttpServletRequest request) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        IPage<CustomerAuditRecordDTO> page = auditRecordService.pageQuery(pageParam, carrierIds);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @GetMapping("getCertificationInfo/{id}")
    @ApiOperation(value = "认证信息", notes = "获取渠道下的认证信息")
    public Result getCertificationInfo(@PathVariable("id") Long id) {
        return Result.SUCCESS(auditRecordService.getCertificationInfo(id));
    }

}

