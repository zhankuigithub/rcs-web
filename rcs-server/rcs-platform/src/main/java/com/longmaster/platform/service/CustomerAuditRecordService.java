package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.customer.CertificationDTO;
import com.longmaster.platform.dto.customer.CustomerAuditRecordDTO;
import com.longmaster.platform.entity.CustomerAuditRecord;

/**
 * <p>
 * 客户审核记录表 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface CustomerAuditRecordService extends IService<CustomerAuditRecord> {

    /**
     * 分压查询客户审核记录
     *
     * @param pageParam
     * @return
     */
    IPage<CustomerAuditRecordDTO> pageQuery(PageParam<CustomerAuditRecordDTO> pageParam, String carrierIds);


    CertificationDTO getCertificationInfo(Long id);
}
