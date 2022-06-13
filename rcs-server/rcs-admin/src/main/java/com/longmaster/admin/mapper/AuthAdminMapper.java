package com.longmaster.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.admin.dto.auth.AuthAdminDto;
import com.longmaster.admin.dto.auth.AuthAdminQueryDto;
import com.longmaster.admin.entity.AuthAdmin;
import org.apache.ibatis.annotations.Param;


public interface AuthAdminMapper extends BaseMapper<AuthAdmin> {

    AuthAdminDto getAdminDetail(String id);

    IPage<AuthAdminDto> pageSelect(IPage<AuthAdminQueryDto> page, @Param("ew") AuthAdminQueryDto params);
}
