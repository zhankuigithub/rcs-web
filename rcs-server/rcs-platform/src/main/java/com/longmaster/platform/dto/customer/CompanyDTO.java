package com.longmaster.platform.dto.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CompanyDTO {

    @ApiModelProperty(value = "客户id")
    @NotNull(message = "请填写客户id")
    private Long customerId;

    @ApiModelProperty(value = "企业名称")
    @NotBlank(message = "请填写企业名称")
    private String companyName;

    @ApiModelProperty(value = "企业地址")
    @NotBlank(message = "请填写企业地址")
    private String companyAddr;

    @ApiModelProperty(value = "企业信用代码")
    @NotBlank(message = "请填写企业信用代码")
    private String companyCode;

    @ApiModelProperty(value = "联系人姓名")
    @NotBlank(message = "请填写联系人姓名")
    private String contactsName;

    @ApiModelProperty(value = "联系电话")
    @NotBlank(message = "请填写联系电话")
    private String contactsPhone;

    @ApiModelProperty(value = "营业执照")
    @NotBlank(message = "请填写营业执照")
    private String businessLicense;

    @ApiModelProperty(value = "备注，介绍")
    @NotBlank(message = "请填写备注")
    private String remark;

}
