package com.longmaster.platform.controller.manage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.longmaster.core.base.SuperEntity;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateAddDTO;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateDTO;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateQueryDTO;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateUpdDTO;
import com.longmaster.platform.entity.MessageTemplate;
import com.longmaster.platform.service.MessageTemplateService;
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
 * 消息模板 前端控制器
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@RestController
@RequestMapping("/manage/messageTemplate")
@Api(value = "MessageTemplateController", tags = "消息模板")
public class MessageTemplateController {

    @Resource
    private MessageTemplateService messageTemplateService;

    @PostMapping("/add")
    @ApiOperation(value = "创建消息模板", notes = "创建消息模板信息")
    public Result createMessageTemplate(@RequestBody @Validated MessageTemplateAddDTO messageTemplate) throws JsonProcessingException {
        Assert.notNull(messageTemplate, new ServerException("消息模板信息不允许为空！"));
        messageTemplateService.addMessageTemplate(messageTemplate);
        return Result.SUCCESS(true);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除消息模板", notes = "删除消息模板信息")
    public Result deleteMessageTemplate(@PathVariable Long id) {
        messageTemplateService.deleteMessageTemplate(id);
        return Result.SUCCESS("删除成功~");
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改消息模板", notes = "修改消息模板信息")
    public Result updateMessageTemplate(@RequestBody @Validated(SuperEntity.Update.class) MessageTemplateUpdDTO messageTemplate) {
        Assert.notNull(messageTemplate, new ServerException("消息模板信息不允许为空！"));
        messageTemplateService.updMessageTemplate(messageTemplate);
        return Result.SUCCESS(true);
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "获取消息模板列表-查询分页数据")
    public Result<PageResult<MessageTemplateDTO>> pageQuery(@RequestBody PageParam<MessageTemplateQueryDTO> pageParam, HttpServletRequest request) {
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        IPage<MessageTemplateDTO> page = messageTemplateService.pageQuery(pageParam, carrierIds);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "模板详情", notes = "模板详情")
    public Result messageTemplateInfo(@PathVariable Long id, HttpServletRequest request) {
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        MessageTemplateDTO messageTemplate = messageTemplateService.getOne(id, carrierIds);
        return Result.SUCCESS(messageTemplate);
    }

    @GetMapping("list")
    @ApiOperation(value = "模板列表", notes = "下拉获取消息模板列表")
    public Result messageTemplateList(Long appId) {
        List<MessageTemplate> items = messageTemplateService.list(new LambdaQueryWrapper<MessageTemplate>()
                .select(MessageTemplate::getId, MessageTemplate::getName, MessageTemplate::getAppId)
                .eq(Objects.nonNull(appId), MessageTemplate::getAppId, appId)
        );
        return Result.SUCCESS(items);
    }

}

