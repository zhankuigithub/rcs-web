package com.longmaster.admin.dto.auth;

import com.longmaster.admin.entity.AuthMenus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PermissionMenusDTO extends AuthMenus {

    @ApiModelProperty("授权ID")
    private String rolePermissionId;

    @ApiModelProperty("拥有权限")
    private String permissions;
}
