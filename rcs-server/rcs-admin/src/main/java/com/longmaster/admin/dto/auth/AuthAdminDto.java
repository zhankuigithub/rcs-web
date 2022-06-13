package com.longmaster.admin.dto.auth;

import com.longmaster.admin.entity.AuthAdmin;
import com.longmaster.admin.entity.AuthRoles;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AuthAdminDto extends AuthAdmin {


    @ApiModelProperty(value = "角色列表")
    private List<AuthRoles> roles;

    @ApiModelProperty("管理员授权角色")
    private List<Long> roleIds;
}
