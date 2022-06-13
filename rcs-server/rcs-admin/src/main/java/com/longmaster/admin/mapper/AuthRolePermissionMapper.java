package com.longmaster.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longmaster.admin.dto.auth.PermissionMenusDTO;
import com.longmaster.admin.entity.AuthRolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface AuthRolePermissionMapper extends BaseMapper<AuthRolePermission> {

    List<PermissionMenusDTO> getRoleMenus(@Param("ids") List<Long> roleIds);
}
