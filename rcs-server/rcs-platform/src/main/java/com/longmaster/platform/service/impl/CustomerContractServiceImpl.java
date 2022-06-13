package com.longmaster.platform.service.impl;

import com.longmaster.platform.entity.CustomerContract;
import com.longmaster.platform.mapper.CustomerContractMapper;
import com.longmaster.platform.service.CustomerContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 合同信息表 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Service
public class CustomerContractServiceImpl extends ServiceImpl<CustomerContractMapper, CustomerContract> implements CustomerContractService {

}
