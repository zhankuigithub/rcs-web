package com.longmaster.rcs.controller.maap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.bean.maap.MaapFile;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.util.JacksonUtil;
import com.longmaster.core.util.TextUtil;
import com.longmaster.core.vo.Result;
import com.longmaster.rcs.service.ICspService;
import com.longmaster.rcs.service.IRcsService;
import com.longmaster.rcs.service.LfasrClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 广西移动相关
 */
@Slf4j
@Api(value = "GxCmccController", tags = "广西移动相关")
@RestController
public class GxCmccController {

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private static final String DESTINATION_GX = "RcsMessageReceiveTopic:GxMobileTag";

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 10,
            TimeUnit.SECONDS, new ArrayBlockingQueue(3), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()); // 将任务交给调用者

    @Resource
    private IRcsService mRcsService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private LfasrClientService lfasrClientService;

    @Resource
    private ICspService cspService;

    @ApiOperation(value = "广西移动上行")
    @PostMapping(value = "/api/callback/InboundMessageNotification/{chatBotId}")
    public Result callback(@RequestBody String body, @PathVariable String chatBotId) {
        log.info("广西移动上行: {}", body);
        MaapMessage maapMessage = mRcsService.parseCm(body);
        if (maapMessage != null) {
            List<MaapFile> maapFile = maapMessage.getMaapFile();
            if (maapFile != null && maapFile.size() > 0) {
                Optional<MaapFile> optional = maapFile.stream().filter(a -> a.getContentType().equals("audio/amr")).findFirst();
                if (optional.isPresent()) {
                    executor.execute(() -> lfasrClientService.dictation(maapMessage, DESTINATION_GX));
                    return Result.SUCCESS("audio queue");
                }
            }
            rocketMQTemplate.asyncSendOrderly(DESTINATION_GX, maapMessage, maapMessage.getUserPhone(), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    try {
                        log.info("生产GxMobileTag：{}，sendResult：{}", sObjectMapper.writeValueAsString(maapMessage), sObjectMapper.writeValueAsString(sendResult));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error(throwable.toString());
                    throwable.printStackTrace();
                }
            });
        }
        return Result.SUCCESS();
    }


    @ApiOperation(value = "广西移动的下行消息回调")
    @PostMapping(value = "/api/callback/DeliveryInfoNotification/{chatBotId}")
    public String deliveryInfoNotification(@RequestBody String reqXmlStr, @PathVariable String chatBotId) {
        log.info("Gx DeliveryInfoNotification :{} ，请求内容:{}", chatBotId, reqXmlStr);
        cspService.saveCmSendMessageStatus(chatBotId, reqXmlStr);
        return "ok";
    }

    @ApiOperation(value = "广西移动的消息测回时的回调")
    @PostMapping(value = "/api/callback/MessageStatusNotification/{chatBotId}")
    public String messageStatusNotification(@RequestBody String reqXmlStr, @PathVariable String chatBotId) {
        log.info("Gx MessageStatusNotification :{} ，请求内容:{}", chatBotId, reqXmlStr);
        return "ok";
    }

    @PostMapping(value = "/api/notify/InboundMessageNotification/{chatBotId}")
    @ApiImplicitParams({@ApiImplicitParam(required = true, paramType = "path", dataType = "String")})
    @ApiOperation(value = "广西移动的文件审核通知，最终的文件url在XML中获取")
    public Result notify(HttpServletRequest request, @RequestBody String reqXmlStr, @PathVariable String chatBotId) {
        String tid = request.getHeader("tid");

        log.info("广西移动的文件审核通知:{}", reqXmlStr);
        if (!TextUtil.isEmpty(tid)) {
            ObjectNode objectNode = (ObjectNode) JacksonUtil.transFromXmlToJson(reqXmlStr);
            objectNode.put("tid", tid);
            objectNode.put("type", "media");
            cspService.auditCm(objectNode);
        }
        return Result.SUCCESS();
    }

}
