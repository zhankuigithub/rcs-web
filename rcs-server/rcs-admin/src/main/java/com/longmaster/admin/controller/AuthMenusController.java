package com.longmaster.admin.controller;


import com.longmaster.admin.annotations.Auth;
import com.longmaster.admin.dto.auth.MenusTreeDTO;
import com.longmaster.admin.entity.AuthMenus;
import com.longmaster.admin.service.AuthMenusService;
import com.longmaster.core.base.SuperController;
import com.longmaster.core.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Slf4j
@RestController
@RequestMapping("/manage/menus")
@Api(value = "AuthMenusController", tags = "权限菜单")
public class AuthMenusController  extends SuperController<AuthMenusService, AuthMenus> {

    @Resource
    private AuthMenusService menusService;

    @GetMapping("/tree/{isShowHidden}")
    @ApiOperation(value = "树形列表")
    public Result tree(@PathVariable boolean isShowHidden) {
        return Result.SUCCESS(menusService.treeList(isShowHidden));
    }

    @Auth
    @GetMapping("/items")
    @ApiOperation(value = "获取菜单")
    public Result getParentMenus(@RequestParam(required = false) Long parentId, Long roleId) {
        MenusTreeDTO parentMenus = menusService.getMenusTagRole(parentId, roleId);
        return Result.SUCCESS(parentMenus);
    }

    @GetMapping("/list")
    @ApiOperation(value = "菜单列表")
    public Result getMenus(@RequestParam(required = false) Long parentId) {
        return Result.SUCCESS(menusService.list());
    }

    @ApiOperation(value = "删除菜单")
    @Override
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.SUCCESS(menusService.delMenu(id));
    }
}

