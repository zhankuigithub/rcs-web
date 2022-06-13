package com.longmaster.platform.service.consumer;


import com.longmaster.platform.dto.message.MaapMessage;
import com.longmaster.platform.service.IPhoneNumberAppBlacklistService;
import com.longmaster.platform.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@RocketMQMessageListener(topic = "RcsMessageReceiveTopic", consumerGroup = "${rocketmq.consumer.group}", selectorExpression = "GxMobileTag || ChinaTelecomTag || LangMaTag || ChinaUnicomTag")
@Component
public class CspPlatformConsumer implements RocketMQListener<MaapMessage> {

    @Resource
    private MessageService messageService;

    @Resource
    private IPhoneNumberAppBlacklistService phoneNumberAppBlacklistService;

    @Override
    public void onMessage(MaapMessage maapMessage) {
        try {
            String destinationAddress = maapMessage.getDestinationAddress();
            boolean b = phoneNumberAppBlacklistService.isContainsBlackList(destinationAddress, maapMessage.getUserPhone());
            if (b) {
                log.info("黑名单：{}", maapMessage);
                return;
            }
            log.info("消费：{}", maapMessage);
            messageService.saveSendQueue(maapMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
