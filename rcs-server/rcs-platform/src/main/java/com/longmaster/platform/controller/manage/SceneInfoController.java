package com.longmaster.platform.controller.manage;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.scene.SceneNodeModifyDTO;
import com.longmaster.platform.dto.scene.SceneQueryDTO;
import com.longmaster.platform.dto.scene.SceneUpdateDTO;
import com.longmaster.platform.entity.SceneInfo;
import com.longmaster.platform.service.SceneInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * author zk
 * date 2021/4/29 14:10
 * description 场景节点
 */
@RestController
@RequestMapping("/manage/scene")
@Api(value = "SceneController", tags = "场景节点")
public class SceneInfoController {

    @Resource
    private SceneInfoService sceneInfoService;

    @PostMapping("/page")
    @ApiOperation(value = "分页查询场景")
    public Result<PageResult<SceneInfo>> pageQuery(@RequestBody PageParam<SceneQueryDTO> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<SceneInfo> page = sceneInfoService.pageQuery(pageParam);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @GetMapping("info/{id}")
    @ApiOperation(value = "场景详情")
    public Result sceneInfo(@PathVariable Long id) {
        return Result.SUCCESS(sceneInfoService.getById(id));
    }

    @DeleteMapping("delete/{id}")
    @ApiOperation(value = "删除场景")
    public Result deleteScene(@PathVariable("id") Long id) {
        return Result.SUCCESS(sceneInfoService.deleteScene(id));
    }

    @PostMapping("openOrClose")
    @ApiOperation(value = "启用/停用场景")
    public Result openOrCloseScene(@RequestBody SceneUpdateDTO request) {
        return Result.SUCCESS(sceneInfoService.openOrClose(request.getId(), request.getStatus()));
    }

    @PostMapping("modify")
    @ApiOperation(value = "变更场景")
    public Result modifyScene(@RequestBody SceneNodeModifyDTO request) throws JsonProcessingException {
        return Result.SUCCESS(sceneInfoService.modifyScene(request));
    }

    @GetMapping("list")
    @ApiOperation(value = "场景列表，下拉框")
    public Result sceneList(Long appId) {
        List<SceneInfo> items = sceneInfoService.list(new LambdaQueryWrapper<SceneInfo>()
                .select(SceneInfo::getId, SceneInfo::getName, SceneInfo::getAppId)
                .eq(SceneInfo::getStatus, 0)
                .eq(SceneInfo::getAppId, appId));
        return Result.SUCCESS(items);
    }

}
