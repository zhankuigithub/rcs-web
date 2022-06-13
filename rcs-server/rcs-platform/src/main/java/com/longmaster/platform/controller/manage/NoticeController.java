package com.longmaster.platform.controller.manage;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.core.base.SuperEntity;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.notice.NoticeQueryDTO;
import com.longmaster.platform.entity.Notice;
import com.longmaster.platform.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/manage/notice")
@Api(value = "NoticeController", tags = "公告")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @PostMapping("/add")
    @ApiOperation(value = "新增公告", notes = "新增公告")
    public Result addNotice(@RequestBody @Validated Notice notice) {
        Assert.notNull(notice, new ServiceException("参数不允许为空"));
        return Result.SUCCESS(noticeService.save(notice));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除公告", notes = "删除公告")
    public Result moveNotice(@PathVariable Long id) {
        Assert.notNull(id, new ServerException("ID不允许为空！"));
        return Result.SUCCESS(noticeService.removeById(id));
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改公告", notes = "修改公告")
    public Result editNotice(@RequestBody @Validated(SuperEntity.Update.class) Notice notice) {
        Assert.notNull(notice, new ServerException("公告信息不允许为空！"));
        return Result.SUCCESS(noticeService.updateById(notice));
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "查询公告列表 csp后台使用")
    public Result<PageResult<Notice>> pageQuery(@RequestBody PageParam<NoticeQueryDTO> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<Notice> page = noticeService.pageQuery(pageParam);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/list/{id}")
    @ApiOperation(value = "列表", notes = "查询公告列表 官网使用")
    public Result<PageResult<Notice>> pageQuery(@PathVariable Long id) {
        return Result.SUCCESS(noticeService.queryList(id));
    }

    @GetMapping("/item/{id}")
    @ApiOperation(value = "详情", notes = "查询公告详情")
    public Result infoNotice(@PathVariable Long id) {
        return Result.SUCCESS(noticeService.getById(id));
    }

}
