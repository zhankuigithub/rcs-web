package com.longmaster.platform.controller.manage;


import com.longmaster.core.base.SuperEntity;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.sensitiveWords.CheckDTO;
import com.longmaster.platform.dto.sensitiveWords.WordIdsDTO;
import com.longmaster.platform.dto.sensitiveWords.WordInfoDTO;
import com.longmaster.platform.entity.SensitiveWords;
import com.longmaster.platform.service.SensitiveWordsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/manage/sensitiveWords")
@Api(value = "SensitiveWordsController", tags = "敏感词管理")
public class SensitiveWordsController {

    @Resource
    private SensitiveWordsService service;

    @PostMapping("add")
    @ApiOperation(value = "增加")
    public Result add(@RequestBody @Validated WordInfoDTO wordInfo) {
        return Result.SUCCESS(service.add(wordInfo));
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除")
    public Result delete(@RequestBody @Validated WordIdsDTO words) {
        return Result.SUCCESS(service.delete(words.getIds()));
    }

    @PutMapping("update")
    @ApiOperation(value = "修改")
    public Result update(@RequestBody @Validated({SuperEntity.Update.class}) SensitiveWords sensitiveWords) {
        return Result.SUCCESS(service.update(sensitiveWords));
    }

    @PostMapping("page")
    @ApiOperation(value = "分页")
    public Result<PageResult<SensitiveWords>> pageQuery(@RequestBody PageParam<SensitiveWords> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        return Result.SUCCESS(service.pageQuery(pageParam));
    }

    @PostMapping("check")
    @ApiOperation(value = "检查敏感词")
    public Result check(@RequestBody @Validated CheckDTO check) {
        return Result.SUCCESS(service.check(check.getContent()));
    }

}

