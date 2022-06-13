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
 * 固定菜单项表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_menu_item")
@ApiModel(value="MenuItem对象", description="固定菜单项表")
public class MenuItem extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属应用")
    @TableField("app_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "父级菜单id")
    @TableField("pid")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long pid;

    @ApiModelProperty(value = "菜单项名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "菜单项类型（0目录菜单，1建议回复，2建议操作）")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "排序权重")
    @TableField("weight")
    private Integer weight;

    @ApiModelProperty(value = "执行荷载")
    @TableField("payload")
    private String payload;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;


}
