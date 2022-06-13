package com.longmaster.platform.dto.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CertificationAddDTO {

    @ApiModelProperty(value = "公司名称")
    @NotNull(message = "请填写公司名称")
    public String companyName;

    @ApiModelProperty(value = "管理员id")
    @NotNull(message = "请填写管理员id")
    public String userId;

    @ApiModelProperty(value = "公司地址")
    @NotNull(message = "请填写公司地址")
    public String companyAddr;

    @ApiModelProperty(value = "统一社会信用代码")
    @NotNull(message = "请填写统一社会信用代码")
    public String companyCode;

    @ApiModelProperty(value = "营业执照")
    @NotNull(message = "请上传营业执照")
    public String businessLicense;

    @ApiModelProperty(value = "联系人姓名")
    @NotNull(message = "请填写联系人姓名")
    public String contactsName;

    @ApiModelProperty(value = "联系电话")
    @NotNull(message = "请填写联系电话")
    public String contactsPhone;

    @ApiModelProperty(value = "备注")
    public String remark;

}
