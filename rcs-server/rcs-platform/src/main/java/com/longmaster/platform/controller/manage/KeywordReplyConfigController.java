package com.longmaster.platform.controller.manage;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.core.base.SuperEntity;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.keyword.KeywordReplyConfigAddDTO;
import com.longmaster.platform.dto.keyword.KeywordReplyConfigDTO;
import com.longmaster.platform.dto.keyword.KeywordReplyConfigQueryDTO;
import com.longmaster.platform.entity.KeywordReplyConfig;
import com.longmaster.platform.service.KeywordReplyConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@RestController
@RequestMapping("/manage/keywordReplyConfig")
@Api(value = "KeywordReplyConfigController", tags = "关键词")
public class KeywordReplyConfigController {

    @Resource
    private KeywordReplyConfigService keywordReplyConfigService;


    @PostMapping("/add")
    @ApiOperation(value = "创建关键词", notes = "创建关键词信息")
    public Result createKeywordReplyConfig(@RequestBody @Validated KeywordReplyConfigAddDTO request) {
        Assert.notNull(request, new ServerException("关键词信息不允许为空！"));

        KeywordReplyConfig config = new KeywordReplyConfig();
        config.setName(request.getName());
        config.setAppId(request.getAppId());
        config.setType(request.getType());
        config.setRuleContent(request.getRuleContent());
        config.setReplyId(request.getReplyId());
        config.setWeight(request.getWeight());
        config.setStatus(request.getStatus());

        keywordReplyConfigService.save(config);
        keywordReplyConfigService.reload(); // 刷新内存中的数据
        return Result.SUCCESS(true);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除关键词", notes = "删除关键词信息")
    public Result deleteKeywordReplyConfig(@PathVariable Long id) {
        Assert.notNull(id, new ServerException("关键词ID不允许为空！"));
        keywordReplyConfigService.removeById(id);
        keywordReplyConfigService.reload(); // 刷新内存中的数据
        return Result.SUCCESS(true);
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改关键词", notes = "修改关键词信息")
    public Result updateKeywordReplyConfig(@RequestBody @Validated(SuperEntity.Update.class) KeywordReplyConfig keywordReplyConfig) {
        Assert.notNull(keywordReplyConfig, new ServerException("关键词信息不允许为空！"));
        keywordReplyConfig.setUpdateDt(LocalDateTime.now());
        keywordReplyConfigService.updateById(keywordReplyConfig);
        keywordReplyConfigService.reload(); // 刷新内存中的数据
        return Result.SUCCESS(true);
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "获取关键词列表-查询分页数据")
    public Result<PageResult<KeywordReplyConfigDTO>> pageQuery(@RequestBody PageParam<KeywordReplyConfigQueryDTO> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<KeywordReplyConfigDTO> page = keywordReplyConfigService.pageQuery(pageParam);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "关键词详情", notes = "获取关键词详细信息")
    public Result keywordReplyConfigInfo(@PathVariable Long id) {
        KeywordReplyConfig keywordReplyConfig = keywordReplyConfigService.getOne(id);
        return Result.SUCCESS(keywordReplyConfig);
    }

}

