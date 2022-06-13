package com.longmaster.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.admin.entity.AuthAdminRole;
import com.longmaster.admin.entity.AuthRoles;
import com.longmaster.admin.mapper.AuthAdminRoleMapper;
import com.longmaster.admin.service.AuthAdminRoleService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AuthAdminRoleServiceImpl extends ServiceImpl<AuthAdminRoleMapper, AuthAdminRole> implements AuthAdminRoleService {

    @Override
    public List<AuthRoles> getAdminRoles(Long id) {
        return baseMapper.selectAdminRoles(id);
    }
}
