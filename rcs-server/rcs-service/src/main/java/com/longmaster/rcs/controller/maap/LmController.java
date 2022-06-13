package com.longmaster.rcs.controller.maap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.core.bean.maap.MaapFile;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.vo.Result;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 朗玛信息
 */
@Slf4j
@Api(value = "LmController", tags = "虚拟终端相关")
@RequestMapping("lm")
@RestController
public class LmController {

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private static final String DESTINATION_LM = "RcsMessageReceiveTopic:LangMaTag";

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
    @ApiOperation(value = "朗玛信息上行")
    public Result lmMessageNotification(@RequestBody String body, @PathVariable("chatBotId") String chatBotId) throws JsonProcessingException {
        log.info("朗玛信息上行: {}", body);
        MaapMessage maapMessage = mRcsService.parseLm(body);

        List<MaapFile> maapFile = maapMessage.getMaapFile();
        if (maapFile != null && maapFile.size() > 0) {
            Optional<MaapFile> optional = maapFile.stream().filter(a -> a.getContentType().equals("audio/amr")).findFirst();
            if (optional.isPresent()) {
                executor.execute(() -> lfasrClientService.dictation(maapMessage, DESTINATION_LM));
                return Result.SUCCESS("audio queue");
            }
        }

        rocketMQTemplate.asyncSendOrderly(DESTINATION_LM, maapMessage, maapMessage.getUserPhone(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                try {
                    log.info("生产LangMaTag：{}，sendResult：{}", sObjectMapper.writeValueAsString(maapMessage), sObjectMapper.writeValueAsString(sendResult));
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
        return Result.SUCCESS();
    }

}
