package com.longmaster.platform.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_notice")
@ApiModel(value = "Notice对象", description = "公告表")
public class Notice extends CommonEntity {

    @ApiModelProperty(value = "公告类型，1全体公告，2定向公告")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "客户ids")
    @TableField("customer_ids")
    private String customerIds;

    @ApiModelProperty(value = "公告标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "公告内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "权重")
    @TableField("weight")
    private Integer weight;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;
}
