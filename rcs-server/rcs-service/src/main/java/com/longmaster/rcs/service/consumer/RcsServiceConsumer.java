package com.longmaster.rcs.service.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.rcs.service.ICspService;
import com.longmaster.rcs.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RocketMQMessageListener(topic = "RcsMessageSendTopic", consumerGroup = "RcsConsumerGroup", selectorExpression = "*", consumeMode = ConsumeMode.ORDERLY)
@Component
public class RcsServiceConsumer implements RocketMQListener<JsonNode> {

    @Resource
    private IMessageService mMessageService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ICspService cspService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public void onMessage(JsonNode message) {
        try {
            String mid = message.get("mid").asText();
            boolean absent = redisTemplate.opsForValue().setIfAbsent(mid, mid); // 不为空，不set并返回false
            if (!absent) {
                log.info("RcsConsumerGroup消费（重复消息）：{}", message);
                return;
            }
            log.info("RcsConsumerGroup消费：{}", message);

            JsonNode data = message.get("data");
            JsonNode info = data.get("info");
            JsonNode messages = data.get("messages");

            String messageStr = messages.toString();

            JsonNode jsonNode = cspService.checkSensitiveWords(messageStr);
            JsonNode sensitiveWords = jsonNode.get("data");
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

            messages = sObjectMapper.readTree(messageStr);

            messages.forEach(item -> {
                ObjectNode msg = (ObjectNode) item;

                // 构建消息体需要
                msg.put("chatBotId", info.get("chatBotId").asText());
                msg.put("messageId", UUID.randomUUID().toString());
                msg.put("conversationId", info.get("conversationId").asText());
                msg.put("contributionId", info.get("contributionId").asText());
                msg.put("userPhone", info.get("userPhone").asText());
                msg.put("isMessageBack", data.has("isMessageBack") && data.get("isMessageBack").asBoolean());

                mMessageService.send(msg);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
