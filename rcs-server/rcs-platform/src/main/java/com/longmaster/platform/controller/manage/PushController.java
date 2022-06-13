package com.longmaster.platform.controller.manage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.event.AddPushEventDTO;
import com.longmaster.platform.dto.event.PushEventDTO;
import com.longmaster.platform.entity.PushEvent;
import com.longmaster.platform.service.MessageService;
import com.longmaster.platform.service.PushEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * author zk
 * date 2021/3/2 16:49
 * description 定时任务
 */
@Slf4j
@RestController
@RequestMapping("/manage/push")
@Api(value = "PushController", tags = "定时任务")
public class PushController {

    @Resource
    private MessageService messageService;

    @Resource
    private PushEventService pushEventService;


    @PostMapping("sendMessage")
    @ApiOperation(value = "发消息", notes = "用于后台向多个用户推送消息模板")
    public Result sendMessage(@RequestBody @Validated AddPushEventDTO request) throws JsonProcessingException {
        return messageService.sendGroupMessage(request);
    }

    @PostMapping("page")
    @ApiOperation(value = "任务列表")
    public Result<PageResult<PushEventDTO>> eventPage(@RequestBody PageParam<PushEvent> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        return Result.SUCCESS(pushEventService.pageQuery(pageParam));
    }

    @PutMapping("cancel")
    @ApiOperation(value = "取消")
    public Result cancel(@RequestBody Map params) {
        Assert.isTrue(params.containsKey("taskId"), new ServiceException("请传入任务id"));
        boolean suspend = pushEventService.cancel((String) params.get("taskId"));
        return suspend ? Result.SUCCESS() : Result.FAILED();
    }

    @PutMapping("activateOrSuspend")
    @ApiOperation(value = "激活/暂停")
    public Result activateOrSuspend(@RequestBody Map params) {
        Assert.isTrue(params.containsKey("taskId"), new ServiceException("请传入任务id"));
        boolean suspend = pushEventService.activateOrSuspend((String) params.get("taskId"));
        return suspend ? Result.SUCCESS() : Result.FAILED();
    }

}
