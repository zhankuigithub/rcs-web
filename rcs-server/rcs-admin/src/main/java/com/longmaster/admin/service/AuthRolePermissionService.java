package com.longmaster.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.admin.dto.auth.PermissionMenusDTO;
import com.longmaster.admin.entity.AuthRolePermission;

import java.util.List;

public interface AuthRolePermissionService extends IService<AuthRolePermission> {

    /**
     * 获取角色授权菜单
     * @param accessToken
     * @return 权限菜单
     */
    List<PermissionMenusDTO> getRoleMenus(String accessToken);

    /**
     * 授权角色
     * @param permissions
     * @return
     */
    long roleAuthorize(List<AuthRolePermission> permissions);
}
