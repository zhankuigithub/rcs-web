package com.longmaster.rcs.service.csp.impl;

import com.longmaster.rcs.service.csp.AuthServiceStrategy;
import org.springframework.stereotype.Service;

@Service("ctAuthServiceStrategy")
public class CtAuthServiceStrategy extends AuthServiceStrategy {

    @Override
    public Long carrierId() {
        return 3L;
    }

}
