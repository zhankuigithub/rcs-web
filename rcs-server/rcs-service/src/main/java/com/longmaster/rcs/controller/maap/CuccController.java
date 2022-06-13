package com.longmaster.rcs.controller.maap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.bean.maap.MaapFile;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.vo.Result;
import com.longmaster.rcs.service.ICspService;
import com.longmaster.rcs.service.IRcsService;
import com.longmaster.rcs.service.LfasrClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * author zk
 * date 2021/10/13 14:22
 * description 联通所有的消息、回调接口
 */
@Slf4j
@RequestMapping("cucc")
@RestController
@Api(value = "CuccController", tags = "联通的消息、回调接口")
public class CuccController {


    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private static final String DESTINATION_CUCC = "RcsMessageReceiveTopic:ChinaUnicomTag";

    @Resource
    private ICspService cspService;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 10,
            TimeUnit.SECONDS, new ArrayBlockingQueue(3), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()); // 将任务交给调用者

    @Resource
    private IRcsService mRcsService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private LfasrClientService lfasrClientService;


    @PostMapping("/messageNotification/{chatBotId}/messages")
    @ApiOperation(value = "中国联通上行")
    public Result messageNotification(@RequestBody String body, @PathVariable("chatBotId") String chatBotId) throws JsonProcessingException {
        log.info("中国联通上行: {}", body);
        MaapMessage maapMessage = mRcsService.parseCt(body);
        if (maapMessage != null) {
            List<MaapFile> maapFile = maapMessage.getMaapFile();
            if (maapFile != null && maapFile.size() > 0) {
                Optional<MaapFile> optional = maapFile.stream().filter(a -> a.getContentType().equals("audio/amr")).findFirst();
                if (optional.isPresent()) {
                    executor.execute(() -> lfasrClientService.dictation(maapMessage, DESTINATION_CUCC));
                    return Result.SUCCESS("audio queue");
                }
            }

            rocketMQTemplate.asyncSendOrderly(DESTINATION_CUCC, maapMessage, maapMessage.getUserPhone(), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    try {
                        log.info("生产ChinaTelecomTag：{}，sendResult：{}", sObjectMapper.writeValueAsString(maapMessage), sObjectMapper.writeValueAsString(sendResult));
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


    @GetMapping("/notifyPath")
    @ApiOperation(value = "联通身份鉴权")
    public Result notifyPath(HttpServletRequest request, HttpServletResponse response) {
        String signature = request.getHeader("signature");
        String timestamp = request.getHeader("timestamp");
        String nonce = request.getHeader("nonce");
        String echoStr = request.getHeader("echoStr");
        String chatBotId = request.getHeader("chatbotId");

        log.info("notifyPath chatBotId:" + chatBotId + ",signature:" + signature + ",timestamp:" + timestamp + ",nonce:" + nonce + ",echoStr:" + echoStr);
        // 获取chatbotId的凭证
        response.setHeader("echoStr", echoStr);
        response.setHeader("appId", "OKYfVd2X"); //
        return Result.SUCCESS();
    }

    @PostMapping("/deliveryNotification/{chatBotId}/status")
    @ApiOperation(value = "联通的下行状态报告")
    public Result status(@RequestBody JsonNode body, HttpServletRequest request, @PathVariable("chatBotId") String chatBotId) {
        String signature = request.getHeader("signature");
        String timestamp = request.getHeader("timestamp");
        String nonce = request.getHeader("nonce");
        log.info("status chatBotId:" + chatBotId + ",signature:" + signature + ",timestamp:" + timestamp + ",nonce:" + nonce + ",body:" + body);
        cspService.saveCtSendMessageStatus(body);
        return Result.SUCCESS();
    }


    @PostMapping("/notifyInfoNotification/{chatBotId}/notice/informationChange")
    @ApiOperation(value = "联通的信息变更回调")
    public Result informationChange(HttpServletRequest request, @PathVariable("chatBotId") String chatBotId) {
        String signature = request.getHeader("signature");
        String timestamp = request.getHeader("timestamp");
        String nonce = request.getHeader("nonce");
        log.info("informationChange chatBotId:" + chatBotId + ",signature:" + signature + ",timestamp:" + timestamp + ",nonce:" + nonce);
        return Result.SUCCESS();
    }


    @PostMapping("/notifyInfoNotification/{chatBotId}/notice/rcsspam")
    @ApiOperation(value = "联通的举报回调")
    public Result rcsspam(@RequestBody String body, HttpServletRequest request, @PathVariable("chatBotId") String chatBotId) {
        String signature = request.getHeader("signature");
        String timestamp = request.getHeader("timestamp");
        String nonce = request.getHeader("nonce");
        log.info("rcsspam chatBotId:" + chatBotId + ",signature:" + signature + ",timestamp:" + timestamp + ",nonce:" + nonce + ",body:" + body);
        return Result.SUCCESS();
    }


    @PostMapping("/notifyInfoNotification/{chatBotId}/check")
    @ApiOperation(value = "联通的审核结果通知回调")
    public Result check(@RequestBody String body, HttpServletRequest request, @PathVariable("chatBotId") String chatBotId) throws JsonProcessingException {
        String signature = request.getHeader("signature");
        String timestamp = request.getHeader("timestamp");
        String nonce = request.getHeader("nonce");
        log.info("check chatBotId:" + chatBotId + ",signature:" + signature + ",timestamp:" + timestamp + ",nonce:" + nonce + ",body:" + body);

        ObjectNode jsonNode = (ObjectNode) sObjectMapper.readTree(body);
        jsonNode.put("chatBotId", chatBotId);
        cspService.auditCu(jsonNode);
        return Result.SUCCESS();
    }


}
