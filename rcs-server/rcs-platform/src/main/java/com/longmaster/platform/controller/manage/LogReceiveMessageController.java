package com.longmaster.platform.controller.manage;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.statistical.StatisticalDTO;
import com.longmaster.platform.entity.LogReceiveMessage;
import com.longmaster.platform.service.LogReceiveMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/manage/logReceiveMessage")
@Api(value = "LogReceiveMessageController", tags = "上行消息")
public class LogReceiveMessageController {

    @Resource
    private LogReceiveMessageService logReceiveMessageService;

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "查询上行消息列表")
    public Result<PageResult<LogReceiveMessage>> pageQuery(@RequestBody PageParam<StatisticalDTO> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<LogReceiveMessage> page = logReceiveMessageService.pageQuery(pageParam);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

}
