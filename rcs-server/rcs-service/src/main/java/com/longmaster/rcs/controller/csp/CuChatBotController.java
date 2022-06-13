package com.longmaster.rcs.controller.csp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.rcs.dto.csp.ChatBotDTO;
import com.longmaster.rcs.dto.csp.DeveloperDTO;
import com.longmaster.rcs.service.channel.ICuService;
import com.longmaster.rcs.service.csp.AuthStrategy;
import com.longmaster.rcs.service.csp.ChatBotStrategy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/csp/chatbot")
@Api(tags = "中国联通-机器人")
public class CuChatBotController {

    @Resource
    private ChatBotStrategy cuChatBotServiceStrategy;

    @Resource
    private AuthStrategy cuAuthServiceStrategy;

    @Resource
    private ICuService cuccService;

    @PostMapping("/cu/uploadFile")
    @ApiOperation("上传机器人资料")
    public JsonNode uploadFile(MultipartFile file) {
        Assert.notNull(file, "请提供有效文件~");
        return cuChatBotServiceStrategy.invokeUploadChatBotFile(file);
    }

    @PostMapping("/cu")
    @ApiOperation("创建机器人(电信)")
    public JsonNode createChatBot(@RequestBody @Validated ChatBotDTO wrapper) throws JsonProcessingException {
        return cuChatBotServiceStrategy.invokeCreateChatBot(wrapper);
    }

    @DeleteMapping("cu")
    @ApiOperation("删除机器人（电信）")
    public JsonNode deleteChatBot(@RequestBody Map<String, Object> params) {
        Assert.isTrue(params.containsKey("accessTagNo"), "accessTagNo 不允许为空");
        return cuChatBotServiceStrategy.invokeDeleteChatbot((String) params.get("accessTagNo"));
    }

    @PutMapping("/cu")
    @ApiOperation("变更机器人(电信)")
    public JsonNode modifyChatBot(@RequestBody @Validated ChatBotDTO wrapper) {
        return cuChatBotServiceStrategy.invokeUpdateChatbot(wrapper);
    }

    @Validated
    @PutMapping("/cu/online")
    @ApiOperation("上线机器人(电信)")
    public JsonNode onlineChatBot(@RequestBody Map<String, Object> params) {
        Assert.notNull(params, "参数允许为空");
        Assert.isTrue(params.containsKey("accessTagNo"), "accessTagNo 不允许为空");
        Assert.isTrue(params.containsKey("type"), "type 不允许为空");
        return cuChatBotServiceStrategy.invokeIsOnlineUpdate((String) params.get("accessTagNo"), (Integer) params.get("type"));
    }

    @PutMapping("/cu/developer")
    @ApiOperation("开发者配置(电信)")
    public JsonNode chatBotDeveloper(@RequestBody @Validated DeveloperDTO developer) {
        return cuAuthServiceStrategy.invokeUpdateDeveloper(developer);
    }

    @GetMapping("/cu/info")
    @ApiOperation("获取机器人详细信息")
    public JsonNode info(String chatBotId) {
        return cuccService.getChatBotInformation(chatBotId);
    }

    @PostMapping("/cu/sendMenu")
    @ApiOperation("提交机器人菜单审核")
    public JsonNode sendMenu(@RequestBody Map<String, Object> params) {
        Assert.notNull(params, "参数不允许为空");
        Assert.isTrue(params.containsKey("chatBotId"), "chatBotId 不允许为空");
        Assert.isTrue(params.containsKey("menuJson"), "menuJson 不允许为空");

        return cuccService.sendMenuAudit((String) params.get("chatBotId"), (String) params.get("menuJson"));
    }

    @PostMapping("/cu/uploadMaterial")
    @ApiOperation("上传机器人消息素材")
    public JsonNode uploadMaterial(@RequestBody Map<String, Object> params) {
        Assert.notNull(params, "参数不允许为空");
        Assert.isTrue(params.containsKey("chatBotId"), "chatBotId 不允许为空");
        Assert.isTrue(params.containsKey("sourceUrl"), "sourceUrl 不允许为空");
        Assert.isTrue(params.containsKey("uploadMode"), "uploadMode 不允许为空");

        return cuccService.uploadFile((String) params.get("chatBotId"), (String) params.get("sourceUrl"), (String) params.get("uploadMode"));
    }

    @DeleteMapping("/cu/deleteMaterial")
    @ApiOperation("删除机器人消息素材")
    public JsonNode deleteMaterial(@RequestBody Map<String, Object> params) {
        Assert.notNull(params, "参数不允许为空");
        Assert.isTrue(params.containsKey("chatBotId"), "chatBotId 不允许为空");
        Assert.isTrue(params.containsKey("sourceUrl"), "sourceUrl 不允许为空");
        return cuccService.deleteFile((String) params.get("chatBotId"), (String) params.get("sourceUrl"));
    }
}
