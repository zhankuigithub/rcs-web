package com.longmaster.admin.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CustomerBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户名称")
    @NotBlank(message = "请填写客户名称", groups = {SuperEntity.Create.class, CT.class})
    private String name;

    @ApiModelProperty(value = "行业类型")
    private String category;

    @ApiModelProperty(value = "企业评级（0金牌级 1银牌级 2铜牌级 3标准级）")
    private Integer grade;

    @ApiModelProperty(value = "企业简介")
    private String details;

    @ApiModelProperty(value = "企业logo")
    private String logoUrl;

    @ApiModelProperty(value = "工作电话")
    private String phoneNum;

    @ApiModelProperty(value = "企业营业执照")
    @NotBlank(message = "请上传企业营业执照", groups = {CT.class})
    private String businessLicense;

    @ApiModelProperty(value = "企业所在地")
    @NotBlank(message = "请填写企业所在地", groups = {CT.class})
    private String address;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String area;

    @ApiModelProperty(value = "状态（0正常 1限制）")
    private Integer status;

    @ApiModelProperty(value = "审核内容")
    private String auditContent;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;
}
