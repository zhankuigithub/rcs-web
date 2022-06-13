package com.longmaster.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longmaster.admin.entity.AuthRoles;

import java.util.List;
import java.util.Map;


public interface AuthRolesMapper extends BaseMapper<AuthRoles> {

    List<Map<String, Object>> getAdminCarrierMap();

}
