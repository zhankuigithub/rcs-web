package com.longmaster.platform.controller.manage;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.core.base.SuperEntity;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.application.ApplicationDTO;
import com.longmaster.platform.entity.Application;
import com.longmaster.platform.service.ApplicationService;
import com.longmaster.platform.util.UrlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 应用信息表 前端控制器
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@RestController

@RequestMapping("/manage/application")
@Api(value = "ApplicationController", tags = "应用信息")
public class ApplicationController {

    @Resource
    private ApplicationService applicationService;

    @GetMapping
    @ApiOperation(value = "应用列表", notes = "下拉获取应用列表信息")
    public Result itemsApplication(Long customerId) {
        List<Application> items = applicationService.list(new LambdaQueryWrapper<Application>()
                        .select(Application::getId, Application::getCustomerId, Application::getName, Application::getLogoUrl, Application::getCategoryIds)
                        .eq(Objects.nonNull(customerId), Application::getCustomerId, customerId)
                //.eq(Application::getStatus, 1) //审核通过
        );
        items.forEach(item -> item.setLogoUrl(UrlUtil.buildMinIoURL(item.getLogoUrl())));
        return Result.SUCCESS(items);
    }

    @GetMapping("/info")
    @ApiOperation(value = "应用详情", notes = "获取应用详细信息")
    public Result getApplication(Long appId, HttpServletRequest request) {
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        return Result.SUCCESS(applicationService.getInfo(appId, carrierIds));
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "分页查询客户审核信息")
    public Result pageApplication(@RequestBody PageParam<ApplicationDTO> pageParam, HttpServletRequest request) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        IPage<ApplicationDTO> page = applicationService.pageQuery(pageParam, carrierIds);
        page.getRecords().forEach(item -> item.setLogoUrl(UrlUtil.buildMinIoURL(item.getLogoUrl())));
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }


    @PostMapping
    @ApiOperation(value = "创建应用", notes = "创建应用信息")
    public Result createApplication(@RequestBody @Validated Application application) {
        application.setLogoUrl(UrlUtil.getURI(application.getLogoUrl()));
        Assert.notNull(application, new ServerException("应用信息不允许为空！"));
        applicationService.validHasApp(application);
        applicationService.save(application);
        return Result.SUCCESS(true);
    }

    @PutMapping
    @ApiOperation(value = "修改应用", notes = "修改应用信息")
    public Result editApplication(@RequestBody @Validated(SuperEntity.Update.class) Application application) {
        application.setLogoUrl(UrlUtil.getURI(application.getLogoUrl()));
        Assert.notNull(application, new ServerException("应用信息不允许为空！"));
        applicationService.validHasApp(application);
        applicationService.updateById(application);
        return Result.SUCCESS(true);
    }

    @PutMapping("/audit")
    @ApiOperation(value = "审核应用", notes = "审核应用信息")
    public Result auditApplication(@RequestBody @Validated(SuperEntity.Update.class) Application application) {
        application.setLogoUrl(UrlUtil.getURI(application.getLogoUrl()));
        Assert.notNull(application, new ServerException("应用信息不允许为空！"));
        application.setLogoUrl(UrlUtil.getURI(application.getLogoUrl()));
        applicationService.auditApplication(application);
        return Result.SUCCESS(true);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除应用", notes = "删除应用信息")
    public Result moveApplication(@PathVariable Long id) {
        Assert.notNull(id, new ServerException("应用ID不允许为空！"));
        applicationService.removeById(id);
        return Result.SUCCESS(true);
    }
}

