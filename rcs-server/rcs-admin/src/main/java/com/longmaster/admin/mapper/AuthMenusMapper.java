package com.longmaster.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longmaster.admin.dto.auth.PermissionMenusDTO;
import com.longmaster.admin.entity.AuthMenus;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface AuthMenusMapper extends BaseMapper<AuthMenus> {

    List<PermissionMenusDTO> getMenusTagRole(@Param("parentId") Long parentId, @Param("roleId") Long roleId, @Param("isAdmin") Boolean isAdmin);

}
