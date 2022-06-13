package com.longmaster.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 角色授权信息
 * </p>
 *
 * @author dengshuihong
 * @since 2021-03-11
 */
@Data
@TableName("t_auth_role_permission")
@ApiModel(value="AuthRolePermission对象", description="角色授权信息")
public class AuthRolePermission {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "角色ID")
    @TableField("role_id")
    @NotNull(message = "角色ID不允许为空~")
    private Long roleId;

    @ApiModelProperty(value = "菜单ID")
    @TableField("menus_id")
    @NotNull(message = "菜单ID不允许为空~")
    private Long menusId;

    @ApiModelProperty(value = "授权")
    @TableField("permissions")
    @NotNull(message = "授权不允许为空~")
    private String permissions;

    @ApiModelProperty(value = "创建时间")
    @TableField("insert_dt")
    private LocalDateTime insertDt;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_dt")
    private LocalDateTime updateDt;

    @ApiModelProperty(value = "状态 0正常、1.异常")
    @TableField("status")
    private Integer status;


}
