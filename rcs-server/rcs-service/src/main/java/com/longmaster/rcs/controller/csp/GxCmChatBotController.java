package com.longmaster.rcs.controller.csp;

import com.longmaster.core.vo.Result;
import com.longmaster.rcs.service.channel.IGxCmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/csp/chatbot")
@Api(tags = "广西移动-机器人")
public class GxCmChatBotController {

    @Resource
    private IGxCmService gxcmccService;

    @PostMapping("/gxCm/uploadMaterial")
    @ApiOperation("上传机器人消息素材")
    public String uploadMaterial(@RequestBody Map<String, Object> params) {
        Assert.notNull(params, "参数不允许为空");
        Assert.isTrue(params.containsKey("chatBotId"), "chatBotId 不允许为空");
        Assert.isTrue(params.containsKey("sourceUrl"), "sourceUrl 不允许为空");

        return gxcmccService.uploadFile((String) params.get("chatBotId"), (String) params.get("sourceUrl"));
    }

    @DeleteMapping("/gxCm/deleteMaterial")
    @ApiOperation("删除机器人消息素材")
    public Result deleteMaterial(@RequestBody Map<String, Object> params) {
        Assert.notNull(params, "参数不允许为空");
        Assert.isTrue(params.containsKey("chatBotId"), "chatBotId 不允许为空");
        Assert.isTrue(params.containsKey("tid"), "tid 不允许为空");

        gxcmccService.deleteFile((String) params.get("chatBotId"), (String) params.get("tid"));
        return Result.SUCCESS();
    }
}
