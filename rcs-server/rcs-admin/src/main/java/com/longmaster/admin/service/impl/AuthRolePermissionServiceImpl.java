package com.longmaster.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.admin.dto.auth.AuthAdminDto;
import com.longmaster.admin.dto.auth.PermissionMenusDTO;
import com.longmaster.admin.entity.AuthRolePermission;
import com.longmaster.admin.entity.AuthRoles;
import com.longmaster.admin.mapper.AuthRolePermissionMapper;
import com.longmaster.admin.service.AuthRolePermissionService;
import com.longmaster.core.exception.AuthException;
import com.longmaster.core.valid.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AuthRolePermissionServiceImpl extends ServiceImpl<AuthRolePermissionMapper, AuthRolePermission> implements AuthRolePermissionService {

    @Resource
    private RedisTemplate redisTemplate;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    /**
     * 查询角色授权菜单
     *
     * @param accessToken
     * @return
     */
    @Override
    public List<PermissionMenusDTO> getRoleMenus(String accessToken) {
        Assert.isTrue(redisTemplate.hasKey(accessToken), new AuthException("登录过期，请重新登录！"));

        AuthAdminDto accountDto = null;
        try {
            accountDto = sObjectMapper.readValue((String) redisTemplate.opsForValue().get(accessToken), AuthAdminDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        List<Long> roleIds = accountDto.getRoles().stream().map(AuthRoles::getId).collect(Collectors.toList());
        return baseMapper.getRoleMenus(roleIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long roleAuthorize(List<AuthRolePermission> permissions) {
        log.info("[roleAuthorize] execute role authorize, next doing params check...");
        Assert.isTrue(CollectionUtil.isNotEmpty(permissions), new IllegalArgumentException("没有要授权的菜单，请先选择！"));

        log.info("[roleAuthorize] begin doing batch remove old role authorize data...");
        permissions.stream().map(AuthRolePermission::getRoleId).forEach(roleId -> {
            remove(new LambdaQueryWrapper<AuthRolePermission>()
                    .eq(AuthRolePermission::getRoleId, roleId));
        });

        log.info("[roleAuthorize] begin doing batch save new role authorize data...");
        for (AuthRolePermission permission : permissions) {
            Assert.isTrue(Objects.nonNull(permission.getMenusId()) && Objects.nonNull(permission.getRoleId()),
                    new IllegalArgumentException("参数错误！菜单ID或角色ID不允许为空"));
            //add new data
            baseMapper.insert(permission);
        }

        log.info("[roleAuthorize] role Authorize info save successful! insert {} row", permissions.size());
        return permissions.size();
    }

}
