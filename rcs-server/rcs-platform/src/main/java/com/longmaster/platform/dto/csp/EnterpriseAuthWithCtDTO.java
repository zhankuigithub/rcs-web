package com.longmaster.platform.dto.csp;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.longmaster.platform.dto.customer.CustomerAuthDTO;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 电信创建客户对象
 */
@Slf4j
@ToString
public class EnterpriseAuthWithCtDTO {

    protected static final String WORK_PHONE_REG = "^0[0-9]{2,3}-[0-9]{7,8}$";
    protected static final String OPERATOR_CARD_REG = "(^[1-9]\\\\d{5}(18|19|20)\\\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\\\d{3}[0-9Xx]$)|(^[1-9]\\\\d{5}\\\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\\\d{3}$)";
    protected static final String OPERATOR_PHONE_REG = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\\\d{8}$";
    protected static final String EMAIL_ADDRESS_REG = "^([a-z0-9A-Z]+[-|\\\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\\\.)+[a-zA-Z]{2,}$";

    @Getter
    @Valid
    @NotNull(message = "参数'rcsRegisterInfo'不允许为空")
    private RcsRegisterInfo rcsRegisterInfo;

    @Getter
    @Valid
    @NotNull(message = "参数'rcsInfo'不允许为空")
    private RcsInfo rcsInfo;

    @Getter
    @Valid
    @NotNull(message = "参数'rcsLegalP'不允许为空")
    private RcsLegalPerson rcsLegalP;

    @Getter
    @Valid
    @NotNull(message = "参数'rcsContractInformation'不允许为空")
    private RcsContractInformation rcsContractInformation;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    public EnterpriseAuthWithCtDTO() {
    }

    public EnterpriseAuthWithCtDTO(RcsRegisterInfo rcsRegisterInfo, RcsInfo rcsInfo, RcsLegalPerson rcsLegalP, RcsContractInformation rcsContractInformation) {
        this.rcsRegisterInfo = rcsRegisterInfo;
        this.rcsInfo = rcsInfo;
        this.rcsLegalP = rcsLegalP;
        this.rcsContractInformation = rcsContractInformation;
    }

    /**
     * Rcs 客户注册基本信息
     */
    @Data
    @Builder
    public static class RcsRegisterInfo {
        @Alias("name")
        @NotBlank(message = "参数'ecName'不允许为空")
        private String ecName;

        @Alias("grade")
        @NotNull(message = "参数'ecGrade'不允许为空")
        private Integer ecGrade;

        @NotBlank(message = "参数'cspEcNo'不允许为空")
        private String cspEcNo;

        @Alias("category")
        @NotNull(message = "参数'businessType'不允许为空")
        private Integer businessType;
    }

    /**
     * 客户基本信息
     */
    @Data
    @Builder
    public static class RcsInfo {

        @Alias("details")
        @NotBlank(message = "参数'introduce'不允许为空")
        private String introduce;

        @Alias("logoUrl")
        @NotBlank(message = "参数'serviceIcon'不允许为空")
        private String serviceIcon;

        @Alias("phoneNum")
        @Pattern(regexp = WORK_PHONE_REG, message = "请正确填写workPhone")
        private String workPhone;

        @NotBlank(message = "参数'businessLicense'不允许为空")
        private String businessLicense;

        @Alias("address")
        @NotBlank(message = "参数'businessAddress'不允许为空")
        private String businessAddress;

        @NotBlank(message = "参数'province'不允许为空")
        private String province;

        @NotBlank(message = "参数'city'不允许为空")
        private String city;

        @NotBlank(message = "参数'area'不允许为空")
        private String area;

        @Alias("name")
        @NotBlank(message = "参数'operatorName'不允许为空")
        private String operatorName;

        @Alias("idCardNo")
        @Pattern(regexp = OPERATOR_CARD_REG, message = "请正确填写operatorCard")
        private String operatorCard;

        @Pattern(regexp = OPERATOR_PHONE_REG, message = "请正确填写operatorPhone")
        private String operatorPhone;

        @Alias("email")
        @Pattern(regexp = EMAIL_ADDRESS_REG, message = "请正确填写emailAddress")
        private String emailAddress;

        @NotEmpty(message = "参数'operatorIdentityPic'不允许为空")
        private String[] operatorIdentityPic;
    }

    /**
     * RCS 企业法人信息
     */
    @Data
    @Builder
    public static class RcsLegalPerson {
        @Alias("name")
        @NotBlank(message = "参数'legalName'不允许为空")
        private String legalName;

        @Alias("idCardNo")
        @NotBlank(message = "参数'legalIdentification'不允许为空")
        private String legalIdentification;

        @NotEmpty(message = "参数'identificationPic'不允许为空")
        private String[] identificationPic;
    }

    /**
     * RCS  企业合同信息
     */
    @Data
    @Builder
    public static class RcsContractInformation {

        @NotBlank(message = "参数'contractNo'不允许为空")
        private String contractNo;

        @NotBlank(message = "参数'name'不允许为空")
        private String name;

        @Alias("effectiveDt")
        @JsonFormat(pattern = "yyyyMMddhhmmss")
        @NotNull(message = "参数'effectiveDate'不允许为空")
        private LocalDateTime effectiveDate;

        @Alias("expireDt")
        @JsonFormat(pattern = "yyyyMMddhhmmss")
        @NotNull(message = "参数'expiryDate'不允许为空")
        private LocalDateTime expiryDate;

        @Alias("renewStatus")
        @NotNull(message = "参数'status'不允许为空")
        private Integer status;

        @Alias("renewDt")
        @JsonFormat(pattern = "yyyyMMddhhmmss")
        private LocalDateTime renewalDate;

        @Alias("accessoryUrl")
        @NotNull(message = "参数'accessory'不允许为空")
        private String accessory;
    }

    /**
     * 构造电信提交数据
     * <p>submit data structure is：</p>
     * <pre>
     *  {
     *      rcsInfo: {},
     *      rcsRegisterInfo: {},
     *      rcsLegalPerson: {},
     *      rcsContractInformation: {}
     *  }
     *  </pre>
     *
     * @param wrapper
     * @return EnterpriseAuthWrapperWithCT
     */
    public static EnterpriseAuthWithCtDTO transform(CustomerAuthDTO wrapper) {
        //builder submit data
        EnterpriseAuthWithCtDTO.RcsInfo rcsInfo = EnterpriseAuthWithCtDTO.RcsInfo.builder().build();
        BeanUtil.copyProperties(wrapper.getCustomer(), rcsInfo);
        BeanUtil.copyProperties(wrapper.getContacts(), rcsInfo);
        rcsInfo.setOperatorPhone(wrapper.getContacts().getPhoneNum());
        rcsInfo.setWorkPhone(wrapper.getCustomer().getPhoneNum()); // 工作电话

        EnterpriseAuthWithCtDTO.RcsRegisterInfo rcsRegisterInfo = EnterpriseAuthWithCtDTO.RcsRegisterInfo.builder().build();
        BeanUtil.copyProperties(wrapper.getCustomer(), rcsRegisterInfo);

        EnterpriseAuthWithCtDTO.RcsLegalPerson rcsLegalPerson = EnterpriseAuthWithCtDTO.RcsLegalPerson.builder().build();
        BeanUtil.copyProperties(wrapper.getLegalPerson(), rcsLegalPerson);

        EnterpriseAuthWithCtDTO.RcsContractInformation rcsContractInformation = EnterpriseAuthWithCtDTO.RcsContractInformation.builder().build();
        BeanUtil.copyProperties(wrapper.getContract(), rcsContractInformation);

        EnterpriseAuthWithCtDTO ctWrapper = new EnterpriseAuthWithCtDTO(rcsRegisterInfo, rcsInfo, rcsLegalPerson, rcsContractInformation);
        log.debug("[asyncSubmitAuditWithCt] submit data structure with: {}", ctWrapper);
        validator.validate(ctWrapper).forEach(exp -> new IllegalArgumentException(exp.getMessage()));
        return ctWrapper;
    }
}
