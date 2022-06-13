package com.longmaster.platform.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.service.StatisticalCtccService;
import com.longmaster.platform.service.StatisticalGxCmccService;
import com.longmaster.platform.service.StatisticalLmService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController("StatisticalControllerApi")
@RequestMapping("api/statistical")
@Api(value = "StatisticalController", tags = "消息控制层")
public class StatisticalController {

    @Resource
    private StatisticalCtccService statisticalCtccService;

    @Resource
    private StatisticalGxCmccService statisticalGxCmccService;

    @Resource
    private StatisticalLmService statisticalLmService;


    /**
     * 保存下行消息
     *
     * @param body
     * @return
     */
    @PostMapping("/ctcc/saveSendMessage")
    public Result ctccSaveSendMessage(@RequestBody String body) throws JsonProcessingException {
        statisticalCtccService.saveSendMessage(body);
        return Result.SUCCESS();
    }

    /**
     * 保存下行消息状态表
     *
     * @param body
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/ctcc/saveSendMessageStatus")
    public Result ctccSaveSendMessageStatus(@RequestBody String body) throws JsonProcessingException {
        statisticalCtccService.saveSendMessageStatus(body);
        return Result.SUCCESS();
    }


    /**
     * 保存下行消息
     *
     * @param body
     * @return
     */
    @PostMapping("/gxCmcc/saveSendMessage/{messageId}")
    public Result gxCmccSaveSendMessage(@RequestBody String body, @PathVariable String messageId) throws JsonProcessingException {
        statisticalGxCmccService.saveSendMessage(body, messageId);
        return Result.SUCCESS();
    }

    /**
     * 保存下行消息状态表
     *
     * @param body
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/gxCmcc/saveSendMessageStatus/{chatBotId}")
    public Result gxCmccSaveSendMessageStatus(@RequestBody String body, @PathVariable String chatBotId) throws JsonProcessingException {
        statisticalGxCmccService.saveSendMessageStatus(body, chatBotId);
        return Result.SUCCESS();
    }

    /**
     * 保存下行消息记录
     *
     * @param body
     * @return
     */
    @PostMapping("/lm/saveSendMessage")
    public Result lmSaveSendMessage(@RequestBody String body) throws JsonProcessingException {
        statisticalLmService.saveSendMessage(body);
        return Result.SUCCESS();
    }

}
