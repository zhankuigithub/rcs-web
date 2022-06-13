package com.longmaster.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 菜单配置
 * </p>
 *
 * @author dengshuihong
 * @since 2021-03-11
 */
@Data
@TableName("t_auth_menus")
@ApiModel(value="AuthMenus对象", description="菜单配置")
public class AuthMenus extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "权限菜单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "父节点ID, null表示一级节点")
    @TableField("pid")
    private Long pid;

//    @ApiModelProperty(value = "名称")
//    @TableField("name")
//    private String name;

    @ApiModelProperty(value = "路由")
    @TableField("path")
    private String path;

    @ApiModelProperty(value = "标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "图标")
    @TableField("icon")
    private String icon;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "隐藏 0.显示，1.隐藏")
    @TableField("hidden")
    private Integer hidden;

    @ApiModelProperty(value = "操作")
    @TableField("operate")
    private String operate;

    @ApiModelProperty(value = "状态 0.正常、1.异常")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField("insert_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime insertDt;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDt;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "组件")
    @TableField("component")
    private String component;


}
