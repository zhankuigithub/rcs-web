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
 * 客户审核记录表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_customer_audit_record")
@ApiModel(value="CustomerAuditRecord对象", description="客户审核记录表")
public class CustomerAuditRecord extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户id")
    @TableField("customer_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "运营商id")
    @TableField("carrier_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long carrierId;

    @ApiModelProperty(value = "NCSP客户识别码")
    @TableField("csp_ec_no")
    private String cspEcNo;

    @ApiModelProperty(value = "客户状态（0待审核 1审核通过 2审核未通过）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "审核内容")
    @TableField("review_data")
    private String reviewData;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;


}
