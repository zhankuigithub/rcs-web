package com.longmaster.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("t_auth_admin_role")
@ApiModel(value = "AuthAdminRole对象", description = "管理员角色表")
public class AuthAdminRole {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "管理员ID")
    @TableField("admin_id")
    private Long adminId;

    @ApiModelProperty(value = "角色ID")
    @TableField("role_id")
    private Long roleId;

    @ApiModelProperty(value = "创建时间")
    @TableField("insert_dt")
    private LocalDateTime insertDt;

}
