package com.longmaster.platform.service.impl;

import com.longmaster.platform.dto.chatbot.ChatBotWrapperDTO;
import com.longmaster.platform.dto.customer.CustomerAuthDTO;
import com.longmaster.platform.dto.customer.CustomerCtAuthDTO;
import com.longmaster.platform.entity.*;
import com.longmaster.platform.enums.UploadType;
import com.longmaster.platform.service.*;
import com.longmaster.platform.util.UrlUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.Validation;
import javax.validation.Validator;

public abstract class AbstractCarrierAdapter implements CarrierStrategy {
    @Resource
    protected RestTemplate balancedRestTemplate;
    @Lazy
    @Resource
    protected ChatbotService chatbotService;
    @Lazy
    @Resource
    protected CustomerService customerService;
    @Lazy
    @Resource
    protected ApplicationService applicationService;
    @Resource
    protected CarrierService carrierService;
    @Resource
    protected CustomerAuditRecordService auditRecordService;

    protected static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Override
    public Object createChatBot(ChatBotWrapperDTO wrapper) {
        return null;
    }

    @Override
    public Object updateChatBot(ChatBotWrapperDTO wrapper) {
        Application application = wrapper.getApplication();
        Chatbot chatbot = wrapper.getChatbot();
        chatbotService.updateById(chatbot);
        application.setLogoUrl(UrlUtil.getURI(application.getLogoUrl()));
        applicationService.updateById(application);
        return true;
    }

    @Override
    public Object createCustomer(Object obj) throws Exception {
        CustomerCtAuthDTO params = (CustomerCtAuthDTO) obj;
        Long customerId = params.getCustomer().getId();
        CustomerAuthDTO wrapper = customerService.getCustomerWrapper(customerId);
        Assert.notNull(wrapper, new ServerException("找到客户相关信息（NOT FIND）"));
        validator.validate(wrapper, CommonEntity.CM.class).forEach(msg -> {
            throw new ServerException(msg.getMessage());
        });
        CustomerAuditRecord audit = auditRecordService.getOne(new LambdaQueryWrapper<CustomerAuditRecord>()
                .eq(CustomerAuditRecord::getCustomerId, customerId)
                .eq(CustomerAuditRecord::getCarrierId, getCarrier())
        );
        CustomerAuditRecord auditRecord = audit == null ? new CustomerAuditRecord() : audit;
        auditRecord.setCustomerId(customerId);
        auditRecord.setCarrierId(getCarrier());
        auditRecord.setStatus(1);
        auditRecord.setReviewData("审核通过");
        auditRecordService.saveOrUpdate(auditRecord);
        return true;
    }

    @Override
    public Object updateCustomer(CustomerCtAuthDTO wrapper) throws Exception {
        return createCustomer(wrapper);
    }

    @Override
    public Object uploadCustomerFile(String fileUrl, UploadType uploadType, Object... args) {
        return null;
    }

    @Override
    public String uploadChatBotFile(String fileUrl) {
        return null;
    }

    @Override
    public Boolean onlineChatBot(Chatbot chabot) {
        return null;
    }

    @Override
    public void developerConfig(ChatbotDeveloper devConfig, String accessTagNo) {

    }
}
