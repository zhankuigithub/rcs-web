package com.longmaster.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.admin.entity.AuthAdminRole;
import com.longmaster.admin.entity.AuthRoles;

import java.util.List;


public interface AuthAdminRoleService extends IService<AuthAdminRole> {

    List<AuthRoles> getAdminRoles(Long id);

}
