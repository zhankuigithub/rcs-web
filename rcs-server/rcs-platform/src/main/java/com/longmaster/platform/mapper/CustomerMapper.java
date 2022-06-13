package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.customer.CustomerDTO;
import com.longmaster.platform.entity.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 客户信息表 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface CustomerMapper extends BaseMapper<Customer> {

    /**
     * 分页查询客户信息
     * @param page  分页参数
     * @param customerDto 可扩展的筛选条件
     * @return
     */
    IPage<CustomerDTO> pageSelect(IPage<CustomerDTO> page, @Param("ew") CustomerDTO customerDto);
}
