package com.longmaster.rcs.service.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.rcs.service.ICspService;
import com.longmaster.rcs.service.IMessageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQReplyListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RocketMQMessageListener(topic = "RcsMessageSendTaskTopic", consumerGroup = "RcsConsumerTaskGroup", selectorExpression = "TaskTag || NotifyTag")
@Component
public class RcsServiceTaskConsumer implements RocketMQReplyListener<JsonNode, String> {

    @Resource
    private IMessageService mMessageService;

    @Resource
    private ICspService cspService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public String onMessage(JsonNode jsonNode) {
        String messageStr = jsonNode.toString();

        JsonNode result = cspService.checkSensitiveWords(messageStr);
        JsonNode sensitiveWords = result.get("data");
        if (sensitiveWords != null) {
            for (JsonNode sensitiveWord : sensitiveWords) {

                StringBuilder sb = new StringBuilder();
                String text = sensitiveWord.asText();

                for (int i = 0; i < text.length(); i++) {
                    sb.append("*");
                }
                messageStr = messageStr.replaceAll(text, sb.toString());
            }
        }

        jsonNode = sObjectMapper.readTree(messageStr);

        try {
            log.info("RcsConsumerTaskGroup消费：{}", jsonNode);
            return mMessageService.sendGroup(jsonNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error " + UUID.randomUUID().toString();
    }
}
