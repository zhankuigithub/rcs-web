package com.longmaster.platform.controller.manage;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.menu.MenuAuditDTO;
import com.longmaster.platform.dto.menu.MenuGroupsDTO;
import com.longmaster.platform.dto.menu.MenuWrapperWithCtDTO;
import com.longmaster.platform.service.MenuGroupsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 固定菜单组 前端控制器
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@RestController
@RequestMapping("/manage/menuGroups")
@Api(value = "MenuGroupsController", tags = "固定菜单")
public class MenuGroupsController {

    @Resource
    private MenuGroupsService menuGroupsService;

    /**
     * author zk
     * date 2021/2/26 11:52
     * description 构建标准菜单json，id : t_menu_groups 的主键
     */
    @GetMapping("menus")
    @ApiOperation("获取菜单配置")
    public Result getMenus(@RequestParam("appId") Long appId) throws JsonProcessingException {
        return Result.SUCCESS(menuGroupsService.getMenu(appId, false));
    }

    @PostMapping("submitAudit")
    @ApiOperation("提交菜单审核")
    public Result submitAudit(@RequestBody MenuAuditDTO request) throws JsonProcessingException {
        return getOperateResult(menuGroupsService.submitAudit(request.getAppId(), request.getCarrierIds()));
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "分页查询固定菜单信息")
    public Result<PageResult<MenuGroupsDTO>> pageMenus(@RequestBody PageParam<MenuGroupsDTO> pageParam, HttpServletRequest request) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        IPage<MenuGroupsDTO> page = menuGroupsService.pageQuery(pageParam, carrierIds);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @PostMapping
    @ApiOperation(value = "保存菜单", notes = "保存固定菜单信息")
    public Result<Boolean> saveMenuWrapper(@RequestBody @Validated MenuWrapperWithCtDTO wrapper) {
        Assert.notNull(wrapper, new ServerException("参数不允许为空！"));
        menuGroupsService.saveMenuOfCt(wrapper);
        return Result.SUCCESS(true);
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "删除固定菜单信息")
    public Result<Boolean> rmMenuConfig(String id) {
        Assert.notNull(id, new ServerException("参数不允许为空！"));
        return Result.SUCCESS(menuGroupsService.delMenuConfig(id));
    }

    @GetMapping("getCurrentUsed")
    @ApiOperation("获取各个运营商下当前使用的菜单")
    public Result getCurrentUsed(@RequestParam("appId") Long appId, HttpServletRequest request) throws JsonProcessingException {
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        return Result.SUCCESS(menuGroupsService.getCurrentUsed(appId, carrierIds));
    }

    public static Result getOperateResult(Map<Long, String> failedIds) {
        if (!failedIds.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (Long id : failedIds.keySet()) {
                builder.append("[");
                builder.append("id=");
                builder.append(id);
                builder.append(", error_msg=");
                builder.append(failedIds.get(id));
                builder.append("]\n");
            }
            return Result.FAILED(builder.toString());
        } else {
            return Result.SUCCESS();
        }
    }
}

