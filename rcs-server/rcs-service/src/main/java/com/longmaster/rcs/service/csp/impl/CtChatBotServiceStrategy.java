package com.longmaster.rcs.service.csp.impl;

import com.longmaster.rcs.service.csp.ChatBotServiceStrategy;
import org.springframework.stereotype.Service;

@Service("ctChatBotServiceStrategy")
public class CtChatBotServiceStrategy extends ChatBotServiceStrategy {
    @Override
    public Long carrierId() {
        return 3L;
    }
}
