package com.longmaster.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.admin.entity.AuthRoles;


public interface AuthRolesService extends IService<AuthRoles> {

    String getAdminCarrierIds(String token);

    String getAdminCarrierIds(Long adminId);

    String getAdminClientIds(String token);

    String getAdminClientIds(Long adminId);

    void reload();

    Long getAdminId(String token);
}
