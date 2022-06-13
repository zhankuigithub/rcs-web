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

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_phone_number_book")
@ApiModel(value = "PhoneNumberBook对象", description = "通讯录表")
public class PhoneNumberBook extends CommonEntity {

    @ApiModelProperty(value = "客户id")
    @TableField("customer_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "联系人姓名")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "联系人电话")
    @TableField("phone_num")
    private String phoneNum;

    @ApiModelProperty(value = "性别（1男 2女）")
    @TableField("sex")
    private Integer sex;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
