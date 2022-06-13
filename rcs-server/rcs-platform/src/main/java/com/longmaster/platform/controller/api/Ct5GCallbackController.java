package com.longmaster.platform.controller.api;

import com.alibaba.fastjson.JSON;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.csp.CallbackAuditDTO;
import com.longmaster.platform.enums.Ct5GAuditEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 电信CSP回调接口 5G ->  CSP
 *
 * @author dengshuihong
 * @since 2021-01-27
 */
@Slf4j
@RestController
@RequestMapping("/api/callback")
@Api(value = "5G2Csp", tags = "电信CSP回调")
public class Ct5GCallbackController {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * Customer 信息变更通知
     *
     * @param json 通知内容
     * @return 处理结果
     */
    @PostMapping("/editOrAddCustomer")
    @ApiModelProperty("客户信息变更回调")
    public Result editOrAddCustomerListener(@RequestBody String json) {
        log.info("[ct editOrAddCustomerListener] 5G -> CSP Customer info update Listener data  is {} ", json);
        return Result.SUCCESS();
    }

    /**
     * chat bot 信息变更通知
     *
     * @param json 通知内容
     * @return 处理结果
     */
    @PostMapping("/editOrAddChatbot")
    @ApiModelProperty("机器人信息变更回调")
    public Result editOrAddChatbotListener(@RequestBody String json) {
        log.info("[ct editOrAddChatBotListener] 5G -> CSP chat bot info update Listener data  is {} ", json);
        return Result.SUCCESS();
    }

    /**
     * chat bot 审核通知
     *
     * @return 处理结果
     */
    @PostMapping("/cspAudit")
    @ApiModelProperty("审核通知回调")
    public Result cspAuditChatbotListener(@RequestBody String json) {
        log.info("[ct cspAuditChatBotListener]  5G -> CSP  audit Listener data  is {} ", json);
        CallbackAuditDTO auditWrap = JSON.parseObject(json, CallbackAuditDTO.class);
        Ct5GAuditEnum.findIn(auditWrap.getType()).getConsumer().accept(applicationContext, auditWrap);
        return Result.SUCCESS();
    }
}
