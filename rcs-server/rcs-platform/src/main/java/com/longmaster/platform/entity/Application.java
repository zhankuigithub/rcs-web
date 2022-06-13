package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * <p>
 * 应用信息表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_application")
@ApiModel(value = "Application对象", description = "应用信息表")
public class Application extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户id")
    @TableField("customer_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "请填写客户ID（customerId）")
    private Long customerId;

    @ApiModelProperty(value = "应用名称")
    @TableField("name")
    @NotBlank(message = "请填写应用名称（name）")
    private String name;

    @ApiModelProperty(value = "应用简介")
    @TableField("details")
    @NotBlank(message = "请填写应用简介（details）")
    private String details;

    @ApiModelProperty(value = "应用logo")
    @TableField("logo_url")
    @NotBlank(message = "请填写应用logo（logoUrl）")
    private String logoUrl;

    @ApiModelProperty(value = "Chatbot签名")
    @TableField("autograph")
    private String autograph;

    @ApiModelProperty(value = "行业ids,多个id间用逗号分隔")
    @TableField("category_ids")
    private String categoryIds;

    @ApiModelProperty(value = "提供者名称")
    @TableField("provider_name")
    private String providerName;

    @ApiModelProperty(value = "是否显示提供者名称（0不显示 1显示）")
    @TableField("is_show_provider")
    @NotNull(message = "请填写是否显示提供者名称（isShowProvider）", groups = {CT.class})
    private String isShowProvider;

    @ApiModelProperty(value = "应用的定制化URL")
    @TableField("callback_url")
    private String callbackUrl;

    @ApiModelProperty(value = "Chatbot 服务条款地址")
    @TableField("tos_url")
    @Pattern(regexp = NET_URL_HTTPS, message = "请填写机器人服务条款地址（tosUrl）", groups = {CT.class})
    private String tosUrl;

    @ApiModelProperty(value = "Chatbot 邮箱")
    @TableField("email")
    @Email(message = "请填写机器人邮箱（email）", groups = {CT.class})
    private String email;

    @ApiModelProperty(value = "Chatbot 官网(主页地址)")
    @TableField("website_url")
    private String websiteUrl;

    @ApiModelProperty(value = "Chatbot 服务电话")
    @TableField("phone_num")
    @Pattern(regexp = PHONE_REG, message = "请填写有效联系电话", groups = {CT.class})
    private String phoneNum;

    @ApiModelProperty(value = "Chatbot 办公地址")
    @TableField("address")
    private String address;

    @ApiModelProperty(value = "Chatbot 地理经度")
    @TableField("longitude")
    @Digits(integer = 3, fraction = 3, message = "不允许为空且仅支持至多3位有效位", groups = CT.class)
    private BigDecimal longitude;

    @ApiModelProperty(value = "Chatbot 地理纬度")
    @TableField("latitude")
    @Digits(integer = 3, fraction = 3, message = "不允许为空且仅支持至多3位有效位", groups = CT.class)
    private BigDecimal latitude;

    @ApiModelProperty(value = "IP 白名单列表,多个ip间用逗号分隔")
    @TableField("white_ips")
    @Pattern(regexp = IP_V4, message = "请填写IP白名单列表（whiteIps）", groups = {CT.class})
    private String whiteIps;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;
}
