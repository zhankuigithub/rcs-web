package com.longmaster.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longmaster.admin.entity.AuthAdminRole;
import com.longmaster.admin.entity.AuthRoles;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface AuthAdminRoleMapper extends BaseMapper<AuthAdminRole> {

    List<AuthRoles> selectAdminRoles(@Param("adminId") Long adminId);
}
