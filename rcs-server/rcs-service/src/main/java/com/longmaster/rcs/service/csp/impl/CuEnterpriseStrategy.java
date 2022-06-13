package com.longmaster.rcs.service.csp.impl;

import com.longmaster.rcs.service.csp.EnterpriseServiceStrategy;
import org.springframework.stereotype.Service;

@Service("cuEnterpriseStrategy")
public class CuEnterpriseStrategy extends EnterpriseServiceStrategy {

    @Override
    public Long carrierId() {
        return 2L;
    }

}
