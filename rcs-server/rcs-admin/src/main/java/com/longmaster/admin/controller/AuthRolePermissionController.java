package com.longmaster.admin.controller;

import com.longmaster.admin.entity.AuthRolePermission;
import com.longmaster.admin.service.AuthRolePermissionService;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/manage/rolePermission")
@Api(value = "AuthRolePermissionController", tags = "角色权限")
public class AuthRolePermissionController {

    @Resource
    private AuthRolePermissionService rolePermissionService;


    @GetMapping("/menus")
    @ApiOperation("获取授权菜单")
    public Result getRoleMenus(@RequestHeader(AuthConstant.TOKEN_KEY) String accessToken) {
        return Result.SUCCESS(rolePermissionService.getRoleMenus(accessToken));
    }


    @PostMapping("/authorize")
    @ApiOperation("角色授权")
    public Result roleAuthorize(@RequestBody @Valid List<AuthRolePermission> permissions) {
        return Result.SUCCESS(rolePermissionService.roleAuthorize(permissions));
    }
}

