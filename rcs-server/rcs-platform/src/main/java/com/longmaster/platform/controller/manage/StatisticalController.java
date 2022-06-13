package com.longmaster.platform.controller.manage;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.group.LogTaskSendEventDTO;
import com.longmaster.platform.dto.receive.LogReceiveMessageDTO;
import com.longmaster.platform.dto.statistical.*;
import com.longmaster.platform.entity.LogSceneMessage;
import com.longmaster.platform.service.StatisticalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/manage/statistical")
@Api(value = "StatisticalController", tags = "统计")
public class StatisticalController {

    @Resource
    private StatisticalService statisticalService;

    @GetMapping("getHomeStatistical/{customerId}")
    @ApiOperation(value = "获取首页数据")
    public Result getHomeStatistical(@PathVariable Long customerId) {
        return Result.SUCCESS(statisticalService.getHomeStatistical(customerId));
    }


    @PostMapping("/group")
    @ApiOperation(value = "群-整体")
    public Result group(@RequestBody EventStatDTO groupEventStat) {
        Assert.isTrue(groupEventStat.getAppIds().size() > 0, new ServerException("请选择应用！"));
        return Result.SUCCESS(statisticalService.getGroupEventStat(groupEventStat));
    }

    @PostMapping("/groupItems")
    @ApiOperation(value = "群-详细数据")
    public Result groupItems(@RequestBody PageParam<SearchDTO> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<LogTaskSendEventDTO> page = statisticalService.groupItems(pageParam);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @PostMapping("/menu")
    @ApiOperation(value = "菜单-整体")
    public Result menu(@RequestBody EventStatDTO groupEventStat) {
        Assert.isTrue(groupEventStat.getAppIds().size() > 0, new ServerException("请选择应用！"));
        return Result.SUCCESS(statisticalService.getMenuEventStat(groupEventStat));
    }

    @PostMapping("/menuItems")
    @ApiOperation(value = "菜单-详细数据")
    public Result menuItems(@RequestBody MenuItemsSearchDTO menuItemsSearch) {
        Assert.isTrue(menuItemsSearch.getAppIds().size() > 0, new ServerException("请选择应用！"));
        return Result.SUCCESS(statisticalService.getAllMenuItems(menuItemsSearch));
    }

    @PostMapping("/messageTemplate")
    @ApiOperation(value = "消息模板-整体")
    public Result messageTemplate(@RequestBody EventStatDTO groupEventStat) {
        Assert.isTrue(groupEventStat.getAppIds().size() > 0, new ServerException("请选择应用！"));
        return Result.SUCCESS(statisticalService.getMessageTemplateEventStat(groupEventStat));
    }

    @PostMapping("/messageTemplateItems")
    @ApiOperation(value = "消息模板-详细数据")
    public Result messageTemplateItems(@RequestBody PageParam<SearchDTO> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<TemplateEventItemsDTO> page = statisticalService.messageTemplateEventItems(pageParam);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @PostMapping("/receive")
    @ApiOperation(value = "上行消息-整体")
    public Result receive(@RequestBody EventStatDTO groupEventStat) {
        Assert.isTrue(groupEventStat.getAppIds().size() > 0, new ServerException("请选择应用！"));
        return Result.SUCCESS(statisticalService.getReceiveEventStat(groupEventStat));
    }


    @PostMapping("/receiveItems")
    @ApiOperation(value = "上行消息-详细数据")
    public Result receiveItems(@RequestBody PageParam<SearchUpDTO> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<LogReceiveMessageDTO> page = statisticalService.logReceiveMessageItems(pageParam);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @PostMapping("/scene")
    @ApiOperation(value = "会话交互-整体")
    public Result scene(@RequestBody EventStatDTO groupEventStat) {
        Assert.isTrue(groupEventStat.getAppIds().size() > 0, new ServerException("请选择应用！"));
        return Result.SUCCESS(statisticalService.getSceneEventStat(groupEventStat));
    }

    @PostMapping("/sceneItems")
    @ApiOperation(value = "会话交互-详细数据")
    public Result logSceneMessageItems(@RequestBody PageParam<SearchUpDTO> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<LogSceneMessage> page = statisticalService.logSceneMessageItems(pageParam);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @PostMapping("sendAndReceiveLog")
    @ApiOperation(value = "收发记录")
    public Result<Map<String, String>> sendAndReceiveLog(@RequestBody PageParam<Map<String, Object>> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        return Result.SUCCESS(statisticalService.selectSendAndReceiveLog(pageParam));
    }

    @PostMapping("chatLog")
    @ApiOperation(value = "会话记录")
    public Result<Map<String, String>> chatLog(@RequestBody PageParam<Map<String, Object>> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        return Result.SUCCESS(statisticalService.selectChatLog(pageParam));
    }

}
