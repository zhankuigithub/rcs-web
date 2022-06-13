package com.longmaster.platform.controller.manage;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.util.ExcelUtil;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.statistical.MessageEventDTO;
import com.longmaster.platform.entity.LogTaskSendEvent;
import com.longmaster.platform.enums.MessageStatusEnum;
import com.longmaster.platform.enums.MessageTemplateBackTypeEnum;
import com.longmaster.platform.enums.MessageTemplateTypeEnum;
import com.longmaster.platform.service.LogTaskSendEventService;
import com.longmaster.core.valid.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/manage/logTaskSendEvent")
@Api(value = "LogTaskSendEventController", tags = "群发历史")
public class LogTaskSendEventController {

    @Resource
    private LogTaskSendEventService logTaskSendEventService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "查询群发消息列表")
    public Result<PageResult<LogTaskSendEvent>> pageQuery(@RequestBody PageParam<MessageEventDTO> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<LogTaskSendEvent> page = logTaskSendEventService.pageQuery(pageParam);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @PostMapping("detailPage")
    @ApiOperation(value = "详情", notes = "详情")
    public Result<PageResult<Map>> detailPage(@RequestParam(required = false, defaultValue = "false") boolean isExcel, HttpServletResponse response, @RequestBody PageParam<Map> pageParam) throws IOException {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        if (isExcel) {
            pageParam.setCurrentPage(1);
            pageParam.setPageSize(100000);
            PageResult<Map> query = logTaskSendEventService.detailPage(pageParam);
            List<Map> items = query.getItems();
            for (Map item : items) {
                item.put("status", MessageStatusEnum.getModeByType(Integer.parseInt(String.valueOf(item.get("status")))));
                item.put("message_template_type", MessageTemplateTypeEnum.getModeByType(Integer.parseInt(String.valueOf(item.get("message_template_type")))));
                item.put("message_template_back_type", MessageTemplateBackTypeEnum.getModeByType(Integer.parseInt(String.valueOf(item.get("message_template_back_type")))));
            }
            ExcelUtil.downloadExcel(response, sObjectMapper.readTree(sObjectMapper.writeValueAsString(query)),
                    Arrays.asList("任务id", "任务名称", "发送号码", "运营商", "发送时间", "发送状态", "发送类型", "模板id", "模板名称", "模板类型", "回落配置"),
                    Arrays.asList("task_id", "name", "phone_num", "carrier_name", "update_dt", "status", "send_des", "message_template_id", "message_template_name", "message_template_type", "message_template_back_type"));
            return null;
        }
        return Result.SUCCESS(logTaskSendEventService.detailPage(pageParam));
    }

}
