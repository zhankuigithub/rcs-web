package com.longmaster.rcs.dto.csp;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 客服资料
 */
@Data
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnterpriseDTO {

    protected static final String WORK_PHONE_REG = "^0[0-9]{2,3}-[0-9]{7,8}$";
    protected static final String OPERATOR_CARD_REG = "(^[1-9]\\\\d{5}(18|19|20)\\\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\\\d{3}[0-9Xx]$)|(^[1-9]\\\\d{5}\\\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\\\d{3}$)";
    protected static final String OPERATOR_PHONE_REG = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\\\d{8}$";
    protected static final String EMAIL_ADDRESS_REG = "^([a-z0-9A-Z]+[-|\\\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\\\.)+[a-zA-Z]{2,}$";


    @Getter
    @ApiModelProperty("客户基本信息")
    @NotNull(message = "参数'rcsInfo'不允许为空")
    private RcsInfo rcsInfo;

    @Getter
    @ApiModelProperty("客户注册信息")
    @NotNull(message = "参数'rcsRegisterInfo'不允许为空")
    private RcsRegisterInfo rcsRegisterInfo;

    @Getter
    @ApiModelProperty("法人信息")
    @NotNull(message = "参数'rcsLegalP'不允许为空")
    private RcsLegalPerson rcsLegalP;

    @Getter
    @ApiModelProperty("合同信息")
    @NotNull(message = "参数'rcsContractInformation'不允许为空")
    private RcsContractInformation rcsContractInformation;


    /**
     * Rcs 客户注册基本信息
     */
    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RcsRegisterInfo {

        @NotBlank(message = "参数'ecName'不允许为空")
        private String ecName;

        @NotBlank(message = "参数'cspEcNo'不允许为空", groups = Update.class)
        private String cspEcNo;

        @NotNull(message = "参数'ecGrade'不允许为空")
        private Integer ecGrade;

        @NotNull(message = "参数'businessType'不允许为空")
        private Integer businessType;
    }

    /**
     * 客户基本信息
     */
    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class RcsInfo {

        @NotBlank(message = "参数'introduce'不允许为空")
        private String introduce;

        @NotBlank(message = "参数'serviceIcon'不允许为空")
        private String serviceIcon;

        @Pattern(regexp = WORK_PHONE_REG, message = "请正确填写workPhone")
        private String workPhone;

        @NotBlank(message = "参数'businessLicense'不允许为空")
        private String businessLicense;

        @NotBlank(message = "参数'businessAddress'不允许为空")
        private String businessAddress;

        @NotBlank(message = "参数'province'不允许为空")
        private String province;

        @NotBlank(message = "参数'city'不允许为空")
        private String city;

        @NotBlank(message = "参数'area'不允许为空")
        private String area;

        @NotBlank(message = "参数'ecName'不允许为空")
        private String operatorName;

        @Pattern(regexp = OPERATOR_CARD_REG, message = "请正确填写operatorCard")
        private String operatorCard;

        @Pattern(regexp = OPERATOR_PHONE_REG, message = "请正确填写operatorPhone")
        private String operatorPhone;

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class RcsLegalPerson {
        @NotBlank(message = "参数'legalName'不允许为空")
        private String legalName;

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class RcsContractInformation {

        @NotBlank(message = "参数'contractNo'不允许为空")
        private String contractNo;

        @NotBlank(message = "参数'name'不允许为空")
        private String name;

        @NotBlank(message = "参数'effectiveDate'不允许为空")
        private String effectiveDate;

        @NotBlank(message = "参数'expiryDate'不允许为空")
        private String expiryDate;

        @NotBlank(message = "参数'status'不允许为空")
        private Integer status;

        @NotBlank(message = "参数'renewalDate'不允许为空")
        private String renewalDate;

        @NotBlank(message = "参数'accessory'不允许为空")
        private String accessory;
    }

    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getCspEcNo() {
        return this.rcsRegisterInfo.cspEcNo;
    }

    public interface Update {
    }
}
