package com.longmaster.core.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.util.StrUtils;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * web 控制层公共接口抽取， 利用spring set注入方式抽取crud公共操作接口
 *
 * @param <T> 具体的Service
 * @author dengshuihong
 */
@Slf4j
public class SuperController<S extends IService<T>, T extends SuperEntity> {

    @Lazy
    @Autowired
    private S baseService;

    @PostMapping("/add")
    @ApiOperation(value = "新增记录", notes = "新增数据")
    public Result<Boolean> save(@RequestBody @Validated T permission) {
        return Result.SUCCESS(baseService.save(permission));
    }

    @PutMapping("/edit")
    @ApiOperation(value = "更新记录", notes = "更新数据")
    public Result<Boolean> update(@RequestBody @Validated(SuperEntity.Update.class) T permission) {
        log.info("request update put params is: {}", permission);
        return Result.SUCCESS(baseService.updateById(permission));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除记录", notes = "通过数据Id删除数据")
    public Result<Boolean> remove(@PathVariable Long id) {
        log.info("request remove delete data of primary key is: {}", id);
        return Result.SUCCESS(baseService.removeById(id));
    }

    @PostMapping("/defaultPage")
    @ApiOperation(value = "默认分页", notes = "默认分页")
    public Result<PageResult<T>> defaultPage(@RequestBody PageParam<T> pp) {
        log.info("request defaultPage with query data , put params is: {}", pp);
        IPage<T> pgRs = baseService.page(pp.getPage(), new QueryWrapper<T>(StrUtils.rebuildEntity(pp.getParams())));
        return Result.SUCCESS(new PageResult<T>(pgRs.getTotal(), pgRs.getRecords()));
    }

    @PostMapping("/list")
    @ApiOperation(value = "条件查询", notes = "条件查询")
    public Result<List<T>> condition(@RequestBody T condition) {
        log.info("request condition with query data is {}", condition);
        List<T> list = baseService.list(new LambdaQueryWrapper<T>(StrUtils.rebuildEntity(condition)));
        return Result.SUCCESS(list);
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "获取详情", notes = "通过数据id查询数据详情")
    public Result<T> get(@PathVariable String id) {
        log.info("request method get with params is : {}", id);
        return Result.SUCCESS(baseService.getById(id));
    }

}
