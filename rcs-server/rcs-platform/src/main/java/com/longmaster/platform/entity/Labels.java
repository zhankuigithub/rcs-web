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
@TableName("t_labels")
@ApiModel(value = "Labels对象", description = "标签表")
public class Labels extends CommonEntity {

    @ApiModelProperty(value = "客户id")
    @TableField("customer_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "类型（1 通讯录）")
    @TableField("type")
    private Integer type;
    public static final int TYPE_ADDRESS_BOOK = 1; // 通讯录

    @ApiModelProperty(value = "标签名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "状态（0正常 1无效）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
