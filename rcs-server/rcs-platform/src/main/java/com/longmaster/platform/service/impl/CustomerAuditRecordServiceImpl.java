package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.customer.CustomerAuditRecordDTO;
import com.longmaster.platform.dto.customer.CertificationDTO;
import com.longmaster.platform.entity.Customer;
import com.longmaster.platform.entity.CustomerAuditRecord;
import com.longmaster.platform.mapper.CustomerAuditRecordMapper;
import com.longmaster.platform.service.CustomerAuditRecordService;
import com.longmaster.platform.service.CustomerService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 客户审核记录表 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Service
public class CustomerAuditRecordServiceImpl extends ServiceImpl<CustomerAuditRecordMapper, CustomerAuditRecord> implements CustomerAuditRecordService {

    @Lazy
    @Resource
    private CustomerService customerService;

    /**
     * 分页查询客户审核信息
     *
     * @param pageParam
     * @return
     */
    @Override
    public IPage<CustomerAuditRecordDTO> pageQuery(PageParam<CustomerAuditRecordDTO> pageParam, String carrierIds) {
        CustomerAuditRecordDTO recordDto = pageParam.getParams();
        IPage<CustomerAuditRecordDTO> page = pageParam.getPage(CustomerAuditRecordDTO.class);
        return baseMapper.pageSelect(page, recordDto, carrierIds);
    }

    @Override
    public CertificationDTO getCertificationInfo(Long id) {
        CertificationDTO certification = new CertificationDTO();
        Customer one = customerService.getOne(new LambdaQueryWrapper<Customer>().eq(Customer::getId, id).select(Customer::getStatus, Customer::getAuditContent));
        if (one != null) {
            certification.setCustomerId(id);
            certification.setStatus(one.getStatus());
            certification.setAuditContent(one.getAuditContent());

            List<CustomerAuditRecord> list = this.list(new LambdaQueryWrapper<CustomerAuditRecord>()
                    .eq(CustomerAuditRecord::getCustomerId, id)
                    .select(CustomerAuditRecord::getStatus, CustomerAuditRecord::getCarrierId, CustomerAuditRecord::getReviewData));

            certification.setCustomerAuditRecords(list);
        }
        return certification;
    }

}
