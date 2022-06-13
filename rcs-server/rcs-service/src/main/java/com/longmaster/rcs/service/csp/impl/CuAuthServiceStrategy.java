package com.longmaster.rcs.service.csp.impl;

import com.longmaster.rcs.service.csp.AuthServiceStrategy;
import org.springframework.stereotype.Service;

@Service("cuAuthServiceStrategy")
public class CuAuthServiceStrategy extends AuthServiceStrategy {

    @Override
    public Long carrierId() {
        return 2L;
    }

}
