package com.longmaster.rcs.controller.maap;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.bean.maap.MaapFile;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.util.JacksonUtil;
import com.longmaster.core.util.TextUtil;
import com.longmaster.core.vo.Result;
import com.longmaster.rcs.entity.ChatBotInfo;
import com.longmaster.rcs.service.ICspService;
import com.longmaster.rcs.service.IRcsService;
import com.longmaster.rcs.service.LfasrClientService;
import com.longmaster.rcs.service.channel.ICmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 中国移动-朗玛通信
 */
@Slf4j
@Api(value = "CtccController", tags = "中国移动相关")
@RequestMapping("cmcc")
@RestController
public class CmccController {

    @Resource
    private ICspService cspService;

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
    private ICmService cmService;

    @Resource
    private RestTemplate balancedRestTemplate;

    @Value("${rcs.csp-platform-url}")
    private String server;

    @PostMapping(value = "/lmtx/callback/InboundMessageNotification/{chatBotId}")
    @ApiOperation(value = "中国移动上行")
    public Object callback(@RequestBody String body, @PathVariable String chatBotId) {
        log.info("中国移动上行：{}", body);
        MaapMessage maapMessage = mRcsService.parseCm(body);
        if (maapMessage != null) {
            List<MaapFile> maapFile = maapMessage.getMaapFile();
            if (maapFile != null && maapFile.size() > 0) {
                Optional<MaapFile> audio = maapFile.stream().filter(a -> a.getContentType().equals("audio/amr")).findFirst();
                if (audio.isPresent()) {
                    executor.execute(() -> lfasrClientService.dictation(maapMessage, DESTINATION_GX));
                    return Result.SUCCESS("audio queue");
                }

                for (MaapFile file : maapFile) {
                    ChatBotInfo chatBotInfo = cspService.getChatBotInfo(maapMessage.getDestinationAddress());
                    byte[] bytes = cmService.downloadFile(chatBotInfo.getChatBotId(), chatBotInfo.getAppId(), chatBotInfo.getAppKey(), file.getUrl());

                    Map<String, Object> map = new HashMap();
                    map.put("bytes", bytes);
                    map.put("fileName", file.getFileName());
                    map.put("contentType", file.getContentType());
                    ResponseEntity<JsonNode> response = balancedRestTemplate.exchange(server + "/api/message/uploadFile", HttpMethod.PUT, new HttpEntity<>(map), JsonNode.class);
                    if (response.getBody().get("code").asInt() == 200) {
                        String data = response.getBody().get("data").asText();
                        file.setMUrl(data);
                    }
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


    @PostMapping(value = "/lmtx/callback/DeliveryInfoNotification/{chatBotId}")
    @ApiOperation(value = "移动的下行消息回调")
    public String deliveryInfoNotificationTmp(@RequestBody String reqXmlStr, @PathVariable String chatBotId) {
        log.info("DeliveryInfoNotification：{}，请求内容：{}", chatBotId, reqXmlStr);
        cspService.saveCmSendMessageStatus(chatBotId, reqXmlStr);
        return "ok";
    }

    @PostMapping(value = "/lmtx/callback/MessageStatusNotification/{chatBotId}")
    @ApiOperation(value = "移动的消息测回时的回调")
    public String messageStatusNotificationTmp(@RequestBody String reqXmlStr, @PathVariable String chatBotId) {
        log.info("MessageStatusNotification：{}，请求内容：{}", chatBotId, reqXmlStr);
        return "ok";
    }

    @PostMapping(value = "/lmtx/notify/InboundMessageNotification/{chatBotId}")
    @ApiOperation(value = "移动的文件审核通知")
    public Result notify(HttpServletRequest request, @RequestBody String reqXmlStr, @PathVariable String chatBotId) {
        String tid = request.getHeader("tid");

        log.info("移动的文件审核通知：{}", reqXmlStr);
        if (!TextUtil.isEmpty(tid)) {
            ObjectNode objectNode = (ObjectNode) JacksonUtil.transFromXmlToJson(reqXmlStr);
            objectNode.put("tid", tid);
            objectNode.put("type", "media");
            cspService.auditCm(objectNode);
        }
        return Result.SUCCESS();
    }

}
