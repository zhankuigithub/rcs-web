package com.longmaster.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.customer.*;
import com.longmaster.platform.entity.*;
import com.longmaster.platform.mapper.CustomerAuditRecordMapper;
import com.longmaster.platform.mapper.CustomerMapper;
import com.longmaster.platform.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * 客户信息表 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Slf4j
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Resource
    private CustomerContactsService contactsService;
    @Resource
    private CustomerContractService contractService;
    @Resource
    private CustomerLegalPersonService legalPersonService;

    @Lazy
    @Resource
    private CustomerAuditRecordService auditRecordService;
    @Resource
    private ApplicationService applicationService;

    @Resource
    private CustomerAuditRecordMapper customerAuditRecordMapper;

    @Resource
    private List<CarrierStrategy> carrierStrategies;

    //审核未通过状态值
    private static final int AUDIT_NO_PASS_STATUS = -1;
    //审核通过
    private static final int AUDIT_PASS_STATUS = 1;
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 保存客户认证信息
     *
     * @param authWrapper 客户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCustomer(CustomerAuthDTO authWrapper) {
        Assert.notNull(authWrapper.getCustomer(), new ServerException("客户信息不允许为空！"));
        Assert.notNull(authWrapper.getContacts(), new ServerException("联系人信息不允许为空！"));
        Assert.notNull(authWrapper.getLegalPerson(), new ServerException("法人信息不允许为空！"));
        // insert customer info
        String name = authWrapper.getCustomer().getName();
        long count = this.count(new LambdaQueryWrapper<Customer>().eq(Customer::getName, name));
        Assert.isTrue(count == 0, new ServerException("客户'%s'已被占用，当前无法使用~", name));
        baseMapper.insert(authWrapper.getCustomer());
        //insert contacts info
        Long customerId = authWrapper.getCustomer().getId();
        authWrapper.getContacts().setCustomerId(customerId);
        contactsService.save(authWrapper.getContacts());
        //insert legal person info
        authWrapper.getLegalPerson().setCustomerId(customerId);
        legalPersonService.save(authWrapper.getLegalPerson());

        log.info("[addCustomer] add customer auth data success!");
    }

    /**
     * 更新客户认证信息
     *
     * @param authWrapper 认证信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyCustomer(CustomerAuthDTO authWrapper) {
        //update customer info
        Optional.ofNullable(authWrapper.getCustomer()).filter(e -> Objects.nonNull(e.getId())).ifPresent(baseMapper::updateById);
        //update contacts info
        Optional.ofNullable(authWrapper.getContacts()).filter(e -> Objects.nonNull(e.getId())).ifPresent(contactsService::updateById);
        //update legal person info
        Optional.ofNullable(authWrapper.getLegalPerson()).filter(e -> Objects.nonNull(e.getId())).ifPresent(legalPersonService::updateById);

        log.info("[modifyCustomer] modify customer auth data success!");
        return true;
    }

    /**
     * 分页获取企业信息
     *
     * @param pageParam 分页查询
     * @return
     */
    @Override
    public IPage<CustomerDTO> pageQuery(PageParam<CustomerDTO> pageParam, String carrierIds) {
        CustomerDTO customerDto = pageParam.getParams();
        IPage<CustomerDTO> page = pageParam.getPage(CustomerDTO.class);
        IPage<CustomerDTO> pageSelect = baseMapper.pageSelect(page, customerDto);
        pageSelect.getRecords().forEach(item -> item.setAuditRecordList(customerAuditRecordMapper.selectAudit(item.getId(), carrierIds)));
        return pageSelect;
    }

    /**
     * 审核客户资料 一审
     *
     * @param customer 审核结果
     * @return
     */
    @Override
    public boolean firstAuditCustomer(Customer customer) {
        Assert.notNull(customer.getId(), new ServerException("主键缺失，无法变更数据！"));
        Assert.notNull(customer.getStatus(), new ServerException("审核状态不允许为空！"));
        Assert.isTrue(StrUtil.isNotBlank(customer.getAuditContent()), new ServerException("填写审核不通过原因！"));
        baseMapper.updateById(customer);
        log.info("[firstAuditCustomer] modify customer status success!");
        return true;
    }

    /**
     * 获取客户基本信息
     *
     * @param id 客户ID
     * @return
     */
    @Override
    public CustomerAuthDTO getCustomerWrapper(Long id) {
        //check param
        Assert.notNull(id, new ServerException("客户ID不允许为空！"));

        //get customer and conversion to dto
        Customer customer = this.getById(id);
        CustomerDTO customerDto = new CustomerDTO();
        BeanUtil.copyProperties(customer, customerDto);

        //get contacts and conversion to dto
        CustomerContacts contacts = contactsService.getOne(new LambdaQueryWrapper<CustomerContacts>().eq(CustomerContacts::getCustomerId, id));
        CustomerContactsDTO contactsDto = new CustomerContactsDTO();
        BeanUtil.copyProperties(contacts, contactsDto);
        Optional.ofNullable(contactsDto.getIdCardUrl()).ifPresent(urls -> contactsDto.setCardUrl(urls.split("\n")));

        //get legal person and conversion to dto
        CustomerLegalPerson legalPerson = legalPersonService.getOne(new LambdaQueryWrapper<CustomerLegalPerson>().eq(CustomerLegalPerson::getCustomerId, id));
        CustomerLegalPersonDTO legalPersonDto = new CustomerLegalPersonDTO();
        BeanUtil.copyProperties(legalPerson, legalPersonDto);

        //after get contract info and cspEcNo with audit pass
        CustomerContract contract = null;
        if (customer.getStatus() == AUDIT_PASS_STATUS) {
            contract = contractService.getOne(new LambdaQueryWrapper<CustomerContract>().eq(CustomerContract::getCustomerId, id));
            /*CustomerAuditRecord auditRecord = auditRecordService.getOne(new LambdaQueryWrapper<CustomerAuditRecord>()
                    .eq(CustomerAuditRecord::getCustomerId, id)
            );
            if (auditRecord != null) {
                customerDto.setCspEcNo(auditRecord.getCspEcNo());
            }*/
        }

        //merger dto with wrapper obj and return
        return CustomerAuthDTO.builder()
                .customer(customerDto)
                .contacts(contactsDto)
                .legalPerson(legalPersonDto)
                .contract(contract)
                .build();
    }

    /**
     * 保存电信客户审核资料 （企业等级、联系人身份证信息、合同信息）
     *
     * @param authWrapper 认证材料
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveCtAuthMaterial(CustomerCtAuthDTO authWrapper, Long carrierId) {
        Assert.notNull(authWrapper, new ServerException("审核材料不允许为空"));

        CustomerAuditRecord auditRecord = auditRecordService.getOne(new LambdaQueryWrapper<CustomerAuditRecord>()
                .eq(CustomerAuditRecord::getCarrierId, carrierId)
                .eq(CustomerAuditRecord::getCustomerId, authWrapper.getCustomer().getId())
        );
        if (auditRecord != null && auditRecord.getStatus() == 0) {
            throw new ServerException("当前客户还在审核中，不允许编辑");
        }

        //modify customer grade info
        Customer customer = authWrapper.getCustomer();
        Assert.notNull(customer, new ServerException("客户信息不允许为空"));
        Assert.notNull(customer.getId(), new ServerException("客户ID不允许为空"));
        Assert.notNull(customer.getGrade(), new ServerException("企业等级不允许为空"));
        customer.setStatus(null);
        customer.setAuditContent(null);
        updateById(customer);

        //modify contacts  ID card info
        CustomerContactsDTO contacts = authWrapper.getContacts();
        contacts.setIdCardUrl(ArrayUtil.join(contacts.getCardUrl(), "\n"));
        Assert.notNull(contacts.getId(), new ServerException("联系人ID不允许为空"));
        Assert.isTrue(StrUtil.isNotBlank(contacts.getIdCardNo()), new ServerException("联系人身份证号不允许为空"));
        Assert.isTrue(ArrayUtil.isNotEmpty(contacts.getCardUrl()) && contacts.getCardUrl().length == 2, new ServerException("联系人身份证正反面不允许为空"));
        contactsService.updateById(contacts);

        //save or update contract info
        CustomerContract contract = authWrapper.getContract();
        contract.setCustomerId(customer.getId());
        validator.validate(contract).forEach(msg -> {
            throw new ServerException(msg.getMessage());
        });
        if (contacts.getId() == null) {
            List<CustomerContract> hasContract = contractService.list(new LambdaQueryWrapper<CustomerContract>().eq(CustomerContract::getCustomerId, customer.getId()));
            Assert.isTrue(CollectionUtil.isEmpty(hasContract), new ServiceException("当前客户合同信息已存在，请勿重复创建！"));
        }
        contractService.saveOrUpdate(contract);

        log.info("[saveCtAuthMaterial] save success!");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCustomer(Long customerId) {
        Assert.notNull(customerId, new ServiceException("客户ID不允许为空"));
        Assert.isTrue(CollectionUtil.isEmpty(applicationService.getAppByCustomerId(customerId)), new ServiceException("对不起，本客户已在运营商开通Chatbot，不能进行删除！"));

        contractService.remove(new LambdaQueryWrapper<CustomerContract>().eq(CustomerContract::getCustomerId, customerId));
        log.info("[deleteCustomer] remove Customer Contract info by customer id is {}", customerId);
        contactsService.remove(new LambdaQueryWrapper<CustomerContacts>().eq(CustomerContacts::getCustomerId, customerId));
        log.info("[deleteCustomer] remove Customer Contacts info by customer id is {}", customerId);
        legalPersonService.remove(new LambdaQueryWrapper<CustomerLegalPerson>().eq(CustomerLegalPerson::getCustomerId, customerId));
        log.info("[deleteCustomer] remove Customer LegalPerson info by customer id is {}", customerId);
        this.removeById(customerId);
        log.info("[deleteCustomer] remove Customer info by id is {}", customerId);

        return true;
    }

    @Override
    public boolean submit(CustomerCtAuthDTO wrapper, Long carrierId) throws Exception {
        Long customerId = wrapper.getCustomer().getId();
        Assert.notNull(customerId, new ServerException("参数customerId不允许为空"));
        Assert.notNull(carrierId, new ServerException("参数carrierId不允许为空"));
        this.saveCtAuthMaterial(wrapper, carrierId);
        for (CarrierStrategy strategy : carrierStrategies) {
            if (strategy.getCarrier().equals(carrierId)) {
                if (wrapper.getContract().getId() == null) {  //没有合同ID为新增
                    strategy.createCustomer(wrapper);
                } else {
                    strategy.updateCustomer(wrapper);
                }
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createCustomer(CertificationAddDTO certification) {

        String name = certification.getCompanyName();
        long count = this.count(new LambdaQueryWrapper<Customer>().eq(Customer::getName, name));
        if (count > 0) {
            return Result.FAILED(String.format("客户'%s'已被占用，当前无法使用~", name));
        }
        // 保存客户（公司）信息
        Customer customer = new Customer();
        customer.setName(name);
        customer.setBusinessLicense(certification.getBusinessLicense());
        customer.setAddress(certification.getCompanyAddr());
        customer.setDetails(certification.getRemark());
        baseMapper.insert(customer);

        //保存联系人信息
        Long customerId = customer.getId();

        CustomerContacts contacts = new CustomerContacts();
        contacts.setCustomerId(customerId);
        contacts.setName(certification.getContactsName());
        contacts.setPhoneNum(certification.getContactsPhone());
        contactsService.save(contacts);

        // 记录一条法人空数据
        CustomerLegalPerson legalPerson = new CustomerLegalPerson();
        legalPerson.setCustomerId(customerId);
        legalPersonService.save(legalPerson);
        return Result.SUCCESS(customerId);
    }

    @Override
    public boolean syncCspCustomerMessage(CompanyDTO company) {
        // 修改客户信息
        Customer customer = new Customer();
        customer.setId(company.getCustomerId());
        customer.setName(company.getCompanyName());
        customer.setAddress(company.getCompanyAddr());
        customer.setBusinessLicense(company.getBusinessLicense());
        customer.setDetails(company.getRemark());
        customer.setStatus(0);
        boolean b1 = this.updateById(customer);

        // 联系人
        UpdateWrapper<CustomerContacts> contacts = new UpdateWrapper<>();
        contacts.eq("customer_id", company.getCustomerId()).set("name", company.getContactsName()).set("phone_num", company.getContactsPhone());
        boolean b2 = contactsService.update(null, contacts);
        return b1 & b2;
    }

}
