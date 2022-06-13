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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 合同信息表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_customer_contract")
@ApiModel(value = "CustomerContract对象", description = "合同信息表")
public class CustomerContract extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户id")
    @TableField("customer_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "合同编号")
    @TableField("contract_no")
    @NotBlank(message = "请填写合同编号", groups = {CT.class})
    private String contractNo;

    @ApiModelProperty(value = "合同名称")
    @TableField("name")
    @NotBlank(message = "请填写合同名称", groups = {CT.class})
    private String name;

    @ApiModelProperty(value = "合同生效时间")
    @TableField("effective_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "请填写合同生效时间", groups = {CT.class})
    private LocalDateTime effectiveDt;

    @TableField("expire_dt")
    @ApiModelProperty(value = "合同到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "请填写合合同到期时间", groups = {CT.class})
    private LocalDateTime expireDt;

    @TableField("renew_status")
    @ApiModelProperty(value = "合同是否续签（1是 2否）")
    @NotNull(message = "请选择合同是否续签", groups = {CT.class})
    private Integer renewStatus;

    @ApiModelProperty(value = "合同续签时间")
    @TableField("renew_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime renewDt;

    @ApiModelProperty(value = "合同附件地址")
    @TableField("accessory_url")
    @NotBlank(message = "请上传合同附件", groups = {CT.class})
    private String accessoryUrl;

    @ApiModelProperty(value = "状态（0正常 1无效）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
