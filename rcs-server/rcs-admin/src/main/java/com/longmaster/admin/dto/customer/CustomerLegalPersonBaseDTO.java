package com.longmaster.admin.dto.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CustomerLegalPersonBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "法人姓名")
    @NotBlank(message = "请填写法人姓名", groups = {SuperEntity.Create.class, CT.class})
    private String name;

    @ApiModelProperty(value = "法人身份证号")
    @NotBlank(message = "请填写法人身份证号", groups = {CT.class})
    private String idCardNo;

    @ApiModelProperty(value = "身份证正面地址")
    @NotBlank(message = "请填写身份证正面地址", groups = {CT.class})
    private String idCardPostiveUrl;

    @ApiModelProperty(value = "身份证反面地址")
    @NotBlank(message = "请填写身份证反面地址", groups = {CT.class})
    private String idCardNegativeUrl;

    @ApiModelProperty(value = "状态（0正常 1无效）")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;

}
