package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.customer.*;
import com.longmaster.platform.entity.Customer;

/**
 * <p>
 * 客户信息表 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface CustomerService extends IService<Customer> {

    /**
     * 创建客户信息
     *
     * @param authWrapper 客户信息
     */
    void addCustomer(CustomerAuthDTO authWrapper);

    /**
     * 更新客户信息
     *
     * @param authWrapper
     * @return
     */
    boolean modifyCustomer(CustomerAuthDTO authWrapper);

    /**
     * 分查询
     *
     * @param pageParam 分页查询
     * @return
     */
    IPage<CustomerDTO> pageQuery(PageParam<CustomerDTO> pageParam, String carrierIds);

    /**
     * 审核客户资料 一审
     *
     * @param customer 审核结果
     * @return
     */
    boolean firstAuditCustomer(Customer customer);

    /**
     * 获取客户认证基础信息
     *
     * @param id
     * @return
     */
    CustomerAuthDTO getCustomerWrapper(Long id);

    /**
     * 保存电信客户审核资料 （企业等级、联系人身份证信息、合同信息）
     *
     * @param authWrapper
     * @return
     */
    boolean saveCtAuthMaterial(CustomerCtAuthDTO authWrapper, Long carrierId);

    /**
     * 删除客户信息
     *
     * @param customerId
     * @return
     */
    Boolean deleteCustomer(Long customerId);

    boolean submit(CustomerCtAuthDTO wrapper, Long carrierId) throws Exception;


    /**
     * 开放平台使用的，提交企业认证（这里只保存企业，联系人的部分信息。剩余信息待运营人员手动为客户处理）
     *
     * @param certification
     * @return
     */
    Result createCustomer(CertificationAddDTO certification);

    /**
     * 开放平台使用的，修改认证信息时
     *
     * @param company
     * @return
     */
    boolean syncCspCustomerMessage(CompanyDTO company);

}
