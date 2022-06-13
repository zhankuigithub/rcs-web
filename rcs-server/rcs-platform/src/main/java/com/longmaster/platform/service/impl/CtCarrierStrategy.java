package com.longmaster.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.platform.dto.chatbot.ChatBotWrapperDTO;
import com.longmaster.platform.dto.csp.ChatBotWithCtDTO;
import com.longmaster.platform.dto.csp.DeveloperDTO;
import com.longmaster.platform.dto.csp.EnterpriseAuthWithCtDTO;
import com.longmaster.platform.dto.customer.CustomerAuthDTO;
import com.longmaster.platform.dto.customer.CustomerCtAuthDTO;
import com.longmaster.platform.entity.*;
import com.longmaster.platform.enums.UploadType;
import com.longmaster.platform.service.ChatbotDeveloperService;
import com.longmaster.platform.service.CustomerAuditRecordService;
import com.longmaster.platform.service.RcsService;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 电信运营商
 * [csp-manage-server] <--http--> [csp-service]
 * [csp-service] --> [CT 5G]
 */
@Slf4j
@Service("ctCarrierStrategy")
public class CtCarrierStrategy extends AbstractCarrierAdapter {

    @Resource
    private ChatbotDeveloperService developerService;
    @Resource
    private CustomerAuditRecordService auditRecordService;

    @Resource
    private RcsService rcsService;

    //审核通过
    private static final int AUDIT_PASS_STATUS = 1;

    private static final Long CT_CARRIER_ID = 3L;  //策略

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Override
    public Long getCarrier() {
        return CT_CARRIER_ID;
    }

    @Override
    public Object createChatBot(ChatBotWrapperDTO wrapper) {
        validator.validate(wrapper, CommonEntity.CT.class).forEach(err -> new ServerException(err.getMessage()));
        return createOrModifyChatBot(wrapper.getChatbot().getId(), HttpMethod.POST);
    }

    @Override
    public Object updateChatBot(ChatBotWrapperDTO wrapper) {
        validator.validate(wrapper, CommonEntity.Update.class, CommonEntity.CT.class).forEach(err -> new ServerException(err.getMessage()));
        return createOrModifyChatBot(wrapper.getChatbot().getId(), HttpMethod.PUT);
    }

    @Override
    public Object createCustomer(Object obj) throws Exception {
        CustomerCtAuthDTO wrapper = (CustomerCtAuthDTO) obj;
        asyncSubmitAuditWithCt(wrapper.getCustomer().getId(), CT_CARRIER_ID);
        return true;
    }

    @Override
    public Object updateCustomer(CustomerCtAuthDTO wrapper) throws Exception {
        asyncSubmitAuditWithCt(wrapper.getCustomer().getId(), CT_CARRIER_ID);
        return true;
    }

    @Override
    public String uploadCustomerFile(String fileUrl, UploadType uploadType, Object... args) {
        return rcsService.ctUploadCustomerFile(fileUrl, uploadType);
    }

    @Override
    public String uploadChatBotFile(String fileUrl) {
        return rcsService.ctUploadChatBotInfoFile(fileUrl);
    }


    @Override
    public Boolean onlineChatBot(Chatbot chatbot) {
        if (chatbot == null) {
            throw new ServerException("参数错误");
        }

        Long id = chatbot.getId();
        ChatbotDeveloper developer = developerService.getOne(new LambdaQueryWrapper<ChatbotDeveloper>().eq(ChatbotDeveloper::getChatBotId, id));
        Assert.notNull(developer, new ServiceException("开发者配置未完善，请先配置~"));

        if (chatbot.getAuditStatus() < 0) {
            throw new ServerException("机器人还未通过审核，无法上线~");
        }

        int type = chatbot.getAuditStatus() == 0 ? 1 : 2;   //1 上线  2下线
        Map<String, Object> param = new HashMap<>();
        param.put("accessTagNo", chatbot.getAccessTagNo());
        param.put("type", type);

        JsonNode result = rcsService.ctOnlineChatBot(param);
        Assert.notNull(result, new ServerException("服务调用失败~"));
        if (result.get("code").asInt() == 40023) {
            throw new ServiceException("当前应用还未通过审核，无法上线！");
        }

        if (result.get("code").asInt() == 0) {
            developerConfig(developer, chatbot.getAccessTagNo());
            int auditStatus = chatbot.getAuditStatus() == 0 ? 1 : 0;
            chatbotService.update(new LambdaUpdateWrapper<Chatbot>().set(Chatbot::getAuditStatus, auditStatus).eq(Chatbot::getId, id));
        } else {
            throw new ServerException("上线失败（%s）", result.get("code").asInt());
        }
        return true;
    }

    @Override
    public void developerConfig(ChatbotDeveloper devConfig, String accessTagNo) {
        DeveloperDTO developer = new DeveloperDTO(devConfig, accessTagNo);
        JsonNode result = rcsService.ctDeveloper(developer);

        Assert.isTrue(result != null && result.get("code").asInt() == 0, new ServerException("服务调用失败~"));
        JsonNode map = result.get("data");
        devConfig.setAppId(map.get("appId").asText());
        devConfig.setAppKey(map.get("appkey").asText());
        developerService.updateById(devConfig);
    }

    /**
     * <p>async submit data to ct csp</p>
     *
     * @param carrierId  运营商ID
     * @param customerId 客户ID
     */
    @Async
    public void asyncSubmitAuditWithCt(Long customerId, Long carrierId) throws Exception {
        CustomerAuthDTO wrapper = validCtAuthCustomer(customerId, carrierId);
        EnterpriseAuthWithCtDTO ctWrapper = EnterpriseAuthWithCtDTO.transform(wrapper);

        //上传联系人身份证正反面
        Optional.ofNullable(wrapper.getContacts()).ifPresent(contacts -> {
            String[] idCard = contacts.getIdCardUrl().split("\n");
            String pic1 = uploadCustomerFile(idCard[0], UploadType.ID_CARD);
            String pic2 = uploadCustomerFile(idCard[1], UploadType.ID_CARD);
            ctWrapper.getRcsInfo().setOperatorIdentityPic(new String[]{pic1, pic2});
        });
        //法人身份证 正反面
        Optional.ofNullable(wrapper.getLegalPerson()).ifPresent(legal -> {
            String negativePic = uploadCustomerFile(legal.getIdCardNegativeUrl(), UploadType.ID_CARD);
            String postPic = uploadCustomerFile(legal.getIdCardPostiveUrl(), UploadType.ID_CARD);
            ctWrapper.getRcsLegalP().setIdentificationPic(new String[]{postPic, negativePic});
        });
        //上传合同
        Optional.ofNullable(wrapper.getContract().getAccessoryUrl()).ifPresent(url -> {
            String accessoryUrl = uploadCustomerFile(url, UploadType.CONTRACT);
            ctWrapper.getRcsContractInformation().setAccessory(accessoryUrl);
        });

        //上传企业logo
        Optional.ofNullable(wrapper.getCustomer().getLogoUrl()).ifPresent(url -> {
            String serviceIcon = uploadCustomerFile(url, UploadType.CUSTOMER_IMAGE);
            ctWrapper.getRcsInfo().setServiceIcon(serviceIcon);
        });

        //上传运营执照
        Optional.ofNullable(wrapper.getCustomer().getBusinessLicense()).ifPresent(url -> {
            String businessLicense = uploadCustomerFile(url, UploadType.CUSTOMER_IMAGE);
            ctWrapper.getRcsInfo().setBusinessLicense(businessLicense);
        });

        JsonNode result = null;
        HttpMethod method = null;
        try {
            //提交rcs-server
            method = StrUtil.isBlank(ctWrapper.getRcsRegisterInfo().getCspEcNo()) ? HttpMethod.POST : HttpMethod.PUT;
            result = rcsService.ctSubmitCustomerAudit(method, ctWrapper);

        } finally {   //记录审核日志结果
            CustomerAuditRecord auditRecord = auditRecordService.getOne(new LambdaQueryWrapper<CustomerAuditRecord>()
                    .eq(CustomerAuditRecord::getCarrierId, carrierId)
                    .eq(CustomerAuditRecord::getCustomerId, customerId)
            );
            auditRecord = auditRecord == null ? new CustomerAuditRecord() : auditRecord;
            auditRecord.setCustomerId(customerId);
            auditRecord.setCarrierId(carrierId);
            if (HttpMethod.POST == method && result != null && result.get("code").asInt() == 0) {

                JsonNode data = result.get("data");
                auditRecord.setCspEcNo(data.has("cspEcNo") ? data.get("cspEcNo").asText() : null);
            }
            boolean auditStatus = result != null && (result.get("code").asInt() == 0 || result.get("code").asInt() == 81002);  // 0 待审核、 1. 审核通过、 2 审核失败
            auditRecord.setStatus(auditStatus ? 0 : 2);
            auditRecord.setReviewData(auditStatus ? "" : "审核不通过（" + result.get("code").asInt() + "，" + result.get("message").asText() + ")");

            auditRecordService.saveOrUpdate(auditRecord);
            log.info("[asyncSubmitAuditWithCt] update {} customer audit info", auditRecord.getId());
        }

        Assert.notNull(result, new ServiceException("服务调用失败(%s)", result.get("code").asInt()));
    }

    protected CustomerAuthDTO validCtAuthCustomer(Long customerId, Long carrierId) throws Exception {
        CustomerAuthDTO wrapper = customerService.getCustomerWrapper(customerId);
        Assert.notNull(wrapper, new ServerException("找到客户相关信息（NOT FIND）"));
        Assert.isTrue(wrapper.getCustomer().getStatus() == AUDIT_PASS_STATUS, new ServiceException("客户%s还未通过平台审核，请先提交平台审核！", wrapper.getCustomer().getName()));
        CustomerAuditRecord auditRecord = auditRecordService.getOne(
                new LambdaQueryWrapper<CustomerAuditRecord>()
                        .eq(CustomerAuditRecord::getCustomerId, customerId)
                        .eq(CustomerAuditRecord::getCarrierId, carrierId)
        );
        //检查是否认证过
        Optional.ofNullable(auditRecord).ifPresent(audit -> {
            wrapper.getCustomer().setCspEcNo(audit.getCspEcNo());
        });

        validator.validate(wrapper, CustomerAuthDTO.AduitCT.class).forEach(msg -> {
            throw new ServerException(msg.getMessage());
        });
        return wrapper;
    }

    /**
     * 检查参数并构建封装数据
     *
     * @return
     */
    private ChatBotWithCtDTO validAndBuilderWrapper(Long chatBotId) {
        Chatbot chatbot = chatbotService.getById(chatBotId);
        org.springframework.util.Assert.notNull(chatbot, "找不到机器人信息");
        Application application = applicationService.getById(chatbot.getAppId());
        ChatBotWithCtDTO ctWrapper = new ChatBotWithCtDTO(application.getWhiteIps(), application.getCategoryIds());
        Carrier carrier = carrierService.getById(chatbot.getCarrierId());
        BeanUtil.copyProperties(application, ctWrapper);
        BeanUtil.copyProperties(chatbot, ctWrapper);
        ctWrapper.setCspCode(carrier.getCspId());
        ctWrapper.setCategory(application.getCategoryIds().split(","));
        ctWrapper.setIpWhiteList(application.getWhiteIps().split(","));
        validator.validate(ctWrapper).forEach(err -> {
            throw new ServerException(err.getMessage());
        });
        return ctWrapper;
    }

    private Boolean createOrModifyChatBot(Long id, HttpMethod httpMethod) {
        ChatBotWithCtDTO wrapper = validAndBuilderWrapper(id);

        String fileUrl = this.uploadChatBotFile(wrapper.getServiceIcon());
        Assert.notNull(fileUrl, new ServiceException("机器人资料上传失败~"));
        wrapper.setServiceIcon(fileUrl);

        String chatBotId = wrapper.getChatBotId();
        //这里是拆分 页面提交的chatbotId 以 xxx:3455@格式提交，若后期选择自动分配chatbotId, 可考虑在此处调整
        String newId = chatBotId.substring(chatBotId.indexOf(":") + 1, chatBotId.lastIndexOf("@"));
        wrapper.setChatBotId(newId);

        log.info("[createOrModifyChatBot] rcs-server response body is {} ", JSON.toJSONString(wrapper));

        if (httpMethod == HttpMethod.POST) {
            JsonNode result = rcsService.ctCreateChatBot(wrapper);
            Assert.isTrue(result != null && result.get("code").asInt() == 0, new ServiceException("下发运营商失败，原因：%s", result.get("message").asText()));

            String accessTagNo = result.get("data").get("accessTagNo").asText();
            chatbotService.update(new LambdaUpdateWrapper<Chatbot>()
                    .set(Chatbot::getAccessTagNo, accessTagNo)
                    .set(Chatbot::getAuditStatus, accessTagNo == null ? -2 : -1)  //状态（-2未通过 -1审核中 0下线 1上线）
                    .set(Chatbot::getReviewData, accessTagNo == null ? "审核未通过" : null)
                    .eq(Chatbot::getId, id)
            );
        } else {
            JsonNode result = rcsService.ctUpdateChatBot(wrapper);

            chatbotService.update(new LambdaUpdateWrapper<Chatbot>()
                    .set(Chatbot::getAuditStatus, -1)  //状态（-2未通过 -1审核中 0下线 1上线）
                    .set(Chatbot::getReviewData, null)
                    .eq(Chatbot::getId, id)
            );
        }
        return true;
    }
}
