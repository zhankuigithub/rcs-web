package com.longmaster.platform.controller.manage;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.util.StrUtils;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.entity.Carrier;
import com.longmaster.platform.entity.ChatbotDeveloper;
import com.longmaster.platform.service.CarrierService;
import com.longmaster.platform.service.ChatbotDeveloperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 机器人开发者信息 前端控制器
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@RestController
@RequestMapping("/manage/chatbotDeveloper")
@Api(value = "ChatbotDeveloperController", tags = "开发者配置")
public class ChatbotDeveloperController {

    @Resource
    private ChatbotDeveloperService developerService;

    @Resource
    private CarrierService carrierService;

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "分页查询客户审核信息")
    public Result pageApplication(@RequestBody PageParam<ChatbotDeveloper> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<ChatbotDeveloper> page = developerService.page(pageParam.getPage(), new LambdaQueryWrapper<>(StrUtils.rebuildEntity(pageParam.getParams())));
        return Result.SUCCESS(new PageResult<ChatbotDeveloper>(page.getTotal(), page.getRecords()));
    }

    @PostMapping("/{carrierId}")
    @ApiOperation("创建开发者配置")
    public Result createDeveloper(@RequestBody @Validated ChatbotDeveloper developer, @PathVariable Long carrierId) {
        Carrier carrier = carrierService.getById(carrierId);
        developer.setToken(carrier.getCspToken());
        developerService.saveOrUpdate(developer);
        return Result.SUCCESS(developer);
    }

    @PutMapping
    @ApiOperation("修改开发者配置")
    public Result editDeveloper(@RequestBody @Validated ChatbotDeveloper developer) {
        developerService.updateById(developer);
        return Result.SUCCESS(developer);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取开发者配置")
    public Result editDeveloper(@PathVariable Long id) {
        return Result.SUCCESS(developerService.getById(id));
    }
}

