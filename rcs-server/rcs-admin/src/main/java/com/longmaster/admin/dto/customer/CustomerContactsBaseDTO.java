package com.longmaster.admin.dto.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CustomerContactsBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "联系人名称")
    @NotBlank(message = "请填写联系人名称", groups = {SuperEntity.Create.class, CT.class})
    private String name;

    @ApiModelProperty(value = "身份证号码")
    private String idCardNo;

    @ApiModelProperty(value = "联系人电话")
    @NotBlank(message = "请填写联系人电话", groups = {CT.class})
    private String phoneNum;

    @ApiModelProperty(value = "联系人邮箱")
    private String email;

    @ApiModelProperty(value = "身份证文件地址")
    private String idCardUrl;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;

}
