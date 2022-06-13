package com.longmaster.platform.controller.manage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.label.LabelQueryDTO;
import com.longmaster.platform.dto.phoneBook.BatchUpdLabelDTO;
import com.longmaster.platform.entity.Labels;
import com.longmaster.platform.service.LabelsService;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/manage/labels")
@Api(value = "LabelsController", tags = "标签信息")
public class LabelsController {

    @Resource
    private LabelsService labelsService;

    @PostMapping("/add")
    @ApiOperation(value = "新增标签", notes = "新增标签")
    public Result addLabels(@RequestBody @Validated Labels label) {
        Assert.notNull(label, new ServiceException("参数不允许为空"));
        return Result.SUCCESS(labelsService.addLabel(label));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除标签", notes = "删除标签")
    public Result moveLabels(@PathVariable Long id) {
        Assert.notNull(id, new ServerException("标签ID不允许为空！"));
        return Result.SUCCESS(labelsService.deleteLabel(id));
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改标签", notes = "修改标签")
    public Result editLabels(@RequestBody @Validated(SuperEntity.Update.class) Labels label) {
        Assert.notNull(label, new ServerException("标签信息不允许为空！"));
        return Result.SUCCESS(labelsService.updLabel(label));
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "查询标签列表")
    public Result<PageResult<Labels>> pageQuery(@RequestBody PageParam<LabelQueryDTO> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<Labels> page = labelsService.pageQuery(pageParam);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/list/{id}")
    @ApiOperation(value = "列表", notes = "标签下拉框")
    public Result list(@PathVariable Long id) {
        List<Labels> list = labelsService.list(new LambdaQueryWrapper<Labels>().eq(Labels::getCustomerId, id));
        return Result.SUCCESS(list);
    }

    @GetMapping("/group/{id}")
    @ApiOperation(value = "标签分组", notes = "标签分组")
    public Result group(@PathVariable Long id) {
        return Result.SUCCESS(labelsService.group(id));
    }

    @PutMapping("batchUpdLabel")
    @ApiOperation(value = "批量修改", notes = "批量修改")
    public Result batchUpdLabel(@RequestBody BatchUpdLabelDTO batchUpdLabel) {
        return Result.SUCCESS(labelsService.batchUpdLabel(batchUpdLabel));
    }

}
