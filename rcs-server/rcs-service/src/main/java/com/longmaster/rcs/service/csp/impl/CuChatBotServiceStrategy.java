package com.longmaster.rcs.service.csp.impl;

import com.longmaster.rcs.service.csp.ChatBotServiceStrategy;
import org.springframework.stereotype.Service;

@Service("cuChatBotServiceStrategy")
public class CuChatBotServiceStrategy extends ChatBotServiceStrategy {
    @Override
    public Long carrierId() {
        return 2L;
    }
}
