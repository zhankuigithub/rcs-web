package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.customer.CustomerAuditRecordDTO;
import com.longmaster.platform.entity.CustomerAuditRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 客户审核记录表 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface CustomerAuditRecordMapper extends BaseMapper<CustomerAuditRecord> {

    /**
     * 分页查询
     *
     * @param page
     * @param recordDto
     * @return
     */
    IPage<CustomerAuditRecordDTO> pageSelect(IPage<CustomerAuditRecordDTO> page, @Param("ew") CustomerAuditRecordDTO recordDto, @Param("carrierIds") String carrierIds);


    List<CustomerAuditRecordDTO> selectAudit(@Param("customerId") Long customerId, @Param("carrierIds") String carrierIds);

}
