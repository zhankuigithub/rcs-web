package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 客户信息表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_customer")
@ApiModel(value="Customer对象", description="客户信息表")
public class  Customer extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @TableField("name")
    @ApiModelProperty(value = "客户名称")
    @NotBlank(message = "请填写客户名称", groups = {Create.class, CT.class})
    private String name;

    @TableField("category")
    @ApiModelProperty(value = "行业类型")
    private String category;

    @ApiModelProperty(value = "企业评级（0金牌级 1银牌级 2铜牌级 3标准级）")
    @TableField("grade")
    private Integer grade;

    @TableField("details")
    @ApiModelProperty(value = "企业简介")
    private String details;

    @TableField("logo_url")
    @ApiModelProperty(value = "企业logo")
    private String logoUrl;

    @ApiModelProperty(value = "工作电话")
    @TableField("phone_num")
    private String phoneNum;

    @ApiModelProperty(value = "企业营业执照")
    @TableField("business_license")
    @NotBlank(message = "请上传企业营业执照", groups = {CT.class})
    private String businessLicense;

    @ApiModelProperty(value = "企业所在地")
    @TableField("address")
    @NotBlank(message = "请填写企业所在地", groups = {CT.class})
    private String address;

    @ApiModelProperty(value = "省份")
    @TableField("province")
    private String province;

    @ApiModelProperty(value = "市")
    @TableField("city")
    private String city;

    @ApiModelProperty(value = "区")
    @TableField("area")
    private String area;

    @ApiModelProperty(value = "状态（0待审核 1已审核 2审核未通过）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "审核内容")
    @TableField("audit_content")
    private String auditContent;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
