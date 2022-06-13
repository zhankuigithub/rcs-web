package com.longmaster.platform.service.impl;

import com.longmaster.platform.entity.CustomerContacts;
import com.longmaster.platform.mapper.CustomerContactsMapper;
import com.longmaster.platform.service.CustomerContactsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户联系人表 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Service
public class CustomerContactsServiceImpl extends ServiceImpl<CustomerContactsMapper, CustomerContacts> implements CustomerContactsService {

}
