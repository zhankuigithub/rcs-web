package com.longmaster.platform.controller.manage;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.chatbot.ChatBotDTO;
import com.longmaster.platform.dto.chatbot.ChatBotWrapperDTO;
import com.longmaster.platform.dto.chatbot.RmDTO;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.service.ChatbotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 机器人信息表 前端控制器
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@RestController
@RequestMapping("/manage/chatbot")
@Api(value = "ChatbotController", tags = "机器人信息")
public class ChatbotController {

    @Resource
    private ChatbotService chatbotService;

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "分页机器人信息")
    public Result<PageResult<ChatBotDTO>> pageChatBot(@RequestBody PageParam<ChatBotDTO> pageParam, HttpServletRequest request) {
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        IPage<ChatBotDTO> page = chatbotService.pageSelect(pageParam, carrierIds);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "机器详情(运营商)", notes = "查询运营商机器详情")
    public Result<ChatBotWrapperDTO> getChatbotDetail(@PathVariable Long id) {
        ChatBotWrapperDTO wrapper = chatbotService.selectDetail(id);
        return Result.SUCCESS(wrapper);
    }

    @GetMapping("/item/{chatBotId}")
    @ApiOperation(value = "机器详情(运营商)", notes = "查询运营商机器详情")
    public Result<ChatBotWrapperDTO> getChatbotDetail(@PathVariable String chatBotId) {
        return Result.SUCCESS(chatbotService.selectDetail(chatBotId));
    }

    @PostMapping("/addChatbot")
    @ApiOperation(value = "创建机器人", notes = "创建机器人信息")
    public Result<Boolean> addChatBot(@RequestBody ChatBotWrapperDTO wrapper) {
        Assert.notNull(wrapper, new ServerException("机器人信息不允许为空！"));
        return Result.SUCCESS(chatbotService.createChatBot(wrapper));
    }


    @PostMapping("/editChatbot")
    @ApiOperation(value = "修改机器人", notes = "修改机器人信息")
    public Result<Boolean> editChatbot(@RequestBody ChatBotWrapperDTO wrapper) {
        Assert.notNull(wrapper, new ServerException("机器人信息不允许为空！"));
        return Result.SUCCESS(chatbotService.editChatBot(wrapper));
    }


    @PutMapping
    @ApiOperation(value = "编辑", notes = "编辑机器人基础信息")
    public Result<Boolean> editChatBot(@RequestBody Chatbot chatbot) {
        Assert.notNull(chatbot, new ServerException("机器人信息不允许为空！"));
        Assert.notNull(chatbot.getId(), new ServiceException("机器人ID 不允许为空~"));
        chatbotService.updateById(chatbot);
        return Result.SUCCESS(true);
    }

    @PutMapping("/online")
    @ApiOperation(value = "上下线", notes = "上下线机器人")
    public Result<Boolean> onlineChatBot(String id) {
        Assert.notNull(id, new ServiceException("机器人ID 不允许为空~"));
        return Result.SUCCESS(chatbotService.onlineChatbot(id));
    }

    @PostMapping("sendCode")
    @ApiOperation(value = "发送验证码", notes = "发送验证码")
    public Result sendCode(HttpServletRequest request) throws JsonProcessingException {
        String token = request.getHeader(AuthConstant.TOKEN_KEY);
        chatbotService.sendCode(token, request.getSession().getId());
        return Result.SUCCESS();
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除", notes = "删除机器人基础信息")
    public Result<Boolean> rmChatBot(HttpServletRequest request, @RequestBody RmDTO rmDTO) {
        return Result.SUCCESS(chatbotService.deleteChatBot(rmDTO.getId(), rmDTO.getCode(), request.getSession().getId()));
    }

    @GetMapping("selectChatBotByCid")
    @ApiOperation(value = "通过客户id获取chatbot", notes = "通过客户id获取chatbot")
    public Result<Boolean> selectChatBotByCid(String customerId) {
        return Result.SUCCESS(chatbotService.selectChatBotByCid(customerId));
    }

}

