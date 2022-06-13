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

/**
 * <p>
 * 法人信息表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_customer_legal_persion")
@ApiModel(value="CustomerLegalPersion对象", description="法人信息表")
public class CustomerLegalPerson extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户id")
    @TableField("customer_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "法人姓名")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "法人身份证号")
    @TableField("id_card_no")
    private String idCardNo;

    @ApiModelProperty(value = "身份证正面地址")
    @TableField("id_card_postive_url")
    private String idCardPostiveUrl;

    @ApiModelProperty(value = "身份证反面地址")
    @TableField("id_card_negative_url")
    private String idCardNegativeUrl;

    @ApiModelProperty(value = "状态（0正常 1无效）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
