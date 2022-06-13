package com.longmaster.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longmaster.admin.entity.AuthRoles;
import com.longmaster.admin.service.AuthRolesService;
import com.longmaster.core.base.SuperController;
import com.longmaster.core.base.SuperEntity;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/manage/roles")
@Api(value = "AuthRolesController", tags = "角色")
public class AuthRolesController extends SuperController<AuthRolesService, AuthRoles> {

    @Resource
    private AuthRolesService rolesService;

    @GetMapping("/list")
    @ApiOperation(value = "获取角色")
    public Result getRoles() {
        List<AuthRoles> roles = rolesService.list(new LambdaQueryWrapper<AuthRoles>()
                .eq(AuthRoles::getStatus, 0)
                .orderByDesc(AuthRoles::getInsertDt)
        );
        return Result.SUCCESS(roles);
    }

    @PostMapping({"/add"})
    @ApiOperation(
            value = "新增记录",
            notes = "新增数据"
    )
    public Result<Boolean> save(@RequestBody @Validated AuthRoles authRoles) {
        boolean save = rolesService.save(authRoles);
        rolesService.reload();
        return Result.SUCCESS(save);
    }

    @PutMapping({"/edit"})
    @ApiOperation(
            value = "更新记录",
            notes = "更新数据"
    )
    public Result<Boolean> update(@RequestBody @Validated({SuperEntity.Update.class}) AuthRoles authRoles) {
        boolean b = rolesService.updateById(authRoles);
        rolesService.reload();
        return Result.SUCCESS(b);
    }

    @DeleteMapping({"/{id}"})
    @ApiOperation(
            value = "删除记录",
            notes = "通过数据Id删除数据"
    )
    public Result<Boolean> remove(@PathVariable Long id) {
        boolean b = rolesService.removeById(id);
        rolesService.reload();
        return Result.SUCCESS(b);
    }

    @GetMapping("getCarrierIds")
    @ApiOperation(
            value = "获取运营商权限",
            notes = "通过管理员Id获取运营商权限"
    )
    public String getCarrierIds(HttpServletRequest request) {
        String accessToken = request.getHeader(AuthConstant.TOKEN_KEY);
        return rolesService.getAdminCarrierIds(accessToken);
    }

    @GetMapping("getClientIds")
    @ApiOperation(
            value = "获取客户端权限",
            notes = "通过管理员Id获取运营商权限"
    )
    public String getClientIds(HttpServletRequest request) {
        String accessToken = request.getHeader(AuthConstant.TOKEN_KEY);
        return rolesService.getAdminClientIds(accessToken);
    }

}

