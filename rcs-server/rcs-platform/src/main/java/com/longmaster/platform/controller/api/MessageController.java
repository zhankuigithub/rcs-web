package com.longmaster.platform.controller.api;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.core.enums.ResultEnum;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.message.FileBodyDTO;
import com.longmaster.platform.dto.message.MessageDTO;
import com.longmaster.platform.dto.message.NotifyBodyDTO;
import com.longmaster.platform.enums.UploadPathEnum;
import com.longmaster.platform.service.MessageService;
import com.longmaster.platform.service.MinioService;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("api/message")
@Api(value = "MessageController", tags = "消息控制层")
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private MinioService minioService;

    /**
     * author zk
     * date 2021/3/15 16:23
     * description 定制化service向此发包
     */
    @PostMapping("receiveCustom")
    @ApiOperation(value = "接收来自定制化服务的包", notes = "用于业务service向此服务发包")
    public Result receiveCustom(@RequestBody JsonNode jsonNode) {
        messageService.receiveCustom(jsonNode);
        return Result.SUCCESS();
    }


    /**
     * author zk
     * date 2021/3/27 11:50
     * description （ege：预约挂号成功推卡片）
     */
    @PostMapping("notify/webapi")
    @ApiOperation(value = "向应用推消息")
    public Result notifyWebapi(@RequestBody NotifyBodyDTO notifyBody) throws TemplateException, IOException {
        log.info("notifyWebapi：{}", JSON.toJSONString(notifyBody));
        return Result.SUCCESS(messageService.notifyWebapi(notifyBody));
    }

    /**
     * author zk
     * date 2021/3/27 11:53
     * description rcs-smc的会诊通知、远程诊断通知
     */
    @PostMapping("notify/smc")
    @ApiOperation(value = "向应用推消息")
    public Result notifySmc(@RequestBody JsonNode jsonNode) {
        log.info("notifySmc：{}", jsonNode.toString());
        return Result.SUCCESS(messageService.notifySmc(jsonNode));
    }

    /**
     * author zk
     * date 2021/5/13 13:42
     * description 通过消息模板id推消息
     */
    @PostMapping("sendMessageByMsgTmpId")
    @ApiOperation(value = "通过模板发消息")
    public Result sendMessageByMsgTmpId(@RequestBody MessageDTO request) {
        messageService.sendMessageByMsgTmpId(request);
        return Result.SUCCESS();
    }

    @PutMapping("uploadFile")
    @ApiOperation(value = "上传文件", notes = "上传文件")
    public Result uploadMaterial(@RequestBody FileBodyDTO fileBody) {
        String fileUrl = minioService.uploadFile(fileBody.getBytes(),
                fileBody.getFileName(),
                new String[]{".jpg", ".png", ".mp3", ".mp4", ".jpeg", ".amr", ".wav"},
                fileBody.getContentType(),
                UploadPathEnum.UNIAPP);
        return new Result<>(ResultEnum.SUCCESS, "", fileUrl);
    }

}
