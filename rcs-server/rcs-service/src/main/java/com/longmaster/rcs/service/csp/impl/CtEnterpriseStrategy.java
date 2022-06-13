package com.longmaster.rcs.service.csp.impl;

import com.longmaster.rcs.service.csp.EnterpriseServiceStrategy;
import org.springframework.stereotype.Service;

@Service("ctEnterpriseStrategy")
public class CtEnterpriseStrategy extends EnterpriseServiceStrategy {

    @Override
    public Long carrierId() {
        return 3L;
    }

}
