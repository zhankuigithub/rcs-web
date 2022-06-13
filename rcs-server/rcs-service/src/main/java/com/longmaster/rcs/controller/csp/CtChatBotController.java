package com.longmaster.rcs.controller.csp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.rcs.dto.csp.ChatBotDTO;
import com.longmaster.rcs.dto.csp.DeveloperDTO;
import com.longmaster.rcs.service.channel.ICtService;
import com.longmaster.rcs.service.csp.AuthStrategy;
import com.longmaster.rcs.service.csp.ChatBotStrategy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/csp/chatbot")
@Api(tags = "中国电信-机器人")
public class CtChatBotController {

    @Resource
    private ChatBotStrategy ctChatBotServiceStrategy;

    @Resource
    private AuthStrategy ctAuthServiceStrategy;

    @Resource
    private ICtService ctccService;

    @PostMapping("/ct/uploadFile")
    @ApiOperation("上传机器人资料")
    public JsonNode uploadFile(MultipartFile file) {
        Assert.notNull(file, "请提供有效文件~");
        return ctChatBotServiceStrategy.invokeUploadChatBotFile(file);
    }

    @PostMapping("/ct")
    @ApiOperation("创建机器人(电信)")
    public JsonNode createChatBot(@RequestBody @Validated ChatBotDTO wrapper) throws JsonProcessingException {
        log.info("[createChatBot] request params is {}", wrapper);
        return ctChatBotServiceStrategy.invokeCreateChatBot(wrapper);
    }

    @DeleteMapping("ct")
    @ApiOperation("删除机器人（电信）")
    public JsonNode deleteChatBot(@RequestBody Map<String, Object> params) {
        Assert.isTrue(params.containsKey("accessTagNo"), "accessTagNo 不允许为空");
        return ctChatBotServiceStrategy.invokeDeleteChatbot((String) params.get("accessTagNo"));
    }

    @PutMapping("/ct")
    @ApiOperation("变更机器人(电信)")
    public JsonNode modifyChatBot(@RequestBody @Validated ChatBotDTO wrapper) {
        log.info("[modifyChatBot] request params is {}", wrapper);
        return ctChatBotServiceStrategy.invokeUpdateChatbot(wrapper);
    }

    @Validated
    @PutMapping("/ct/online")
    @ApiOperation("变更机器人(电信)")
    public JsonNode onlineChatBot(@RequestBody Map<String, Object> params) {
        Assert.notNull(params, "参数不允许为空");
        Assert.isTrue(params.containsKey("accessTagNo"), "accessTagNo 不允许为空");
        Assert.isTrue(params.containsKey("type"), "type 不允许为空");
        return ctChatBotServiceStrategy.invokeIsOnlineUpdate((String) params.get("accessTagNo"), (Integer) params.get("type"));
    }

    @PutMapping("/ct/developer")
    @ApiOperation("开发者配置(电信)")
    public JsonNode chatBotDeveloper(@RequestBody @Validated DeveloperDTO developer) {
        log.info("[chatBotDeveloper] request params is {}", developer);
        return ctAuthServiceStrategy.invokeUpdateDeveloper(developer);
    }

    @GetMapping("/ct/info")
    @ApiOperation("获取机器人详细信息")
    public JsonNode info(String chatBotId) {
        return ctccService.getChatBotInformation(chatBotId);
    }

    @PostMapping("/ct/sendMenu")
    @ApiOperation("提交机器人菜单审核")
    public JsonNode sendMenu(@RequestBody Map<String, Object> params) {
        Assert.notNull(params, "参数不允许为空");
        Assert.isTrue(params.containsKey("chatBotId"), "chatBotId 不允许为空");
        Assert.isTrue(params.containsKey("menuJson"), "menuJson 不允许为空");

        return ctccService.sendMenuAudit((String) params.get("chatBotId"), (String) params.get("menuJson"));
    }

    @PostMapping("/ct/uploadMaterial")
    @ApiOperation("上传机器人消息素材")
    public JsonNode uploadMaterial(@RequestBody Map<String, Object> params) {
        Assert.notNull(params, "参数不允许为空");
        Assert.isTrue(params.containsKey("chatBotId"), "chatBotId 不允许为空");
        Assert.isTrue(params.containsKey("sourceUrl"), "sourceUrl 不允许为空");
        Assert.isTrue(params.containsKey("uploadMode"), "uploadMode 不允许为空");

        return ctccService.uploadFile((String) params.get("chatBotId"), (String) params.get("sourceUrl"), (String) params.get("uploadMode"));
    }

    @DeleteMapping("/ct/deleteMaterial")
    @ApiOperation("删除机器人消息素材")
    public JsonNode deleteMaterial(@RequestBody Map<String, Object> params) {
        Assert.notNull(params, "参数不允许为空");
        Assert.isTrue(params.containsKey("chatBotId"), "chatBotId 不允许为空");
        Assert.isTrue(params.containsKey("sourceUrl"), "sourceUrl 不允许为空");
        return ctccService.deleteFile((String) params.get("chatBotId"), (String) params.get("sourceUrl"));
    }
}
