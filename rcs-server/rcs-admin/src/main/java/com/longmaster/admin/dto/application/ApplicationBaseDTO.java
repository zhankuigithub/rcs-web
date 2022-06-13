package com.longmaster.admin.dto.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class ApplicationBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "请填写客户ID（customerId）")
    private Long customerId;

    @ApiModelProperty(value = "应用名称")
    @NotBlank(message = "请填写应用名称（name）")
    private String name;

    @ApiModelProperty(value = "应用简介")
    @NotBlank(message = "请填写应用简介（details）")
    private String details;

    @ApiModelProperty(value = "应用logo")
    @NotBlank(message = "请填写应用logo（logoUrl）")
    private String logoUrl;

    @ApiModelProperty(value = "Chatbot签名")
    private String autograph;

    @ApiModelProperty(value = "行业ids,多个id间用逗号分隔")
    private String categoryIds;

    @ApiModelProperty(value = "提供者名称")
    private String providerName;

    @ApiModelProperty(value = "是否显示提供者名称（0不显示 1显示）")
    @NotNull(message = "请填写是否显示提供者名称（isShowProvider）", groups = {CT.class})
    private String isShowProvider;

    @ApiModelProperty(value = "应用的定制化URL")
    private String callbackUrl;

    @ApiModelProperty(value = "Chatbot 服务条款地址")
    @Pattern(regexp = NET_URL_HTTPS, message = "请填写机器人服务条款地址（tosUrl）", groups = {CT.class})
    private String tosUrl;

    @ApiModelProperty(value = "Chatbot 邮箱")
    @Email(message = "请填写机器人邮箱（email）", groups = {CT.class})
    private String email;

    @ApiModelProperty(value = "Chatbot 官网(主页地址)")
    private String websiteUrl;

    @ApiModelProperty(value = "Chatbot 服务电话")
    @Pattern(regexp = PHONE_REG, message = "请填写有效联系电话", groups = {CT.class})
    private String phoneNum;

    @ApiModelProperty(value = "Chatbot 办公地址")
    private String address;

    @ApiModelProperty(value = "Chatbot 地理经度")
    @Digits(integer = 3, fraction = 3, message = "不允许为空且仅支持至多3位有效位", groups = CT.class)
    private BigDecimal longitude;

    @ApiModelProperty(value = "Chatbot 地理纬度")
    @Digits(integer = 3, fraction = 3, message = "不允许为空且仅支持至多3位有效位", groups = CT.class)
    private BigDecimal latitude;

    @ApiModelProperty(value = "IP 白名单列表,多个ip间用逗号分隔")
    @Pattern(regexp = IP_V4, message = "请填写IP白名单列表（whiteIps）", groups = {CT.class})
    private String whiteIps;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;

}
