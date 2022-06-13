package com.longmaster.platform.controller.api;

import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.message.MessageDTO;
import com.longmaster.platform.service.ChatbotDeveloperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * author zk
 * date 2021/2/25 13:14
 * description 获取chatbot
 */
@RestController
@RequestMapping("api/chatBot")
@Api(value = "ChatBotController", tags = "获取chatBot信息")
public class ChatBotController {

    @Resource
    private ChatbotDeveloperService chatbotDeveloperService;


    @PostMapping("getChatBotInfo")
    @ApiOperation(value = "获取chaBot信息", notes = "用于rcs-service获取一条chaBot信息")
    public Result getChatBotInfo(@RequestBody MessageDTO request) {
        return Result.SUCCESS(chatbotDeveloperService.getOneChatBotDeveloper(request.getChatBotId()));
    }

}
