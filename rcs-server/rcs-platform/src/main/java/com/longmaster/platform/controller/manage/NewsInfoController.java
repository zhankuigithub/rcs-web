package com.longmaster.platform.controller.manage;

import com.longmaster.core.vo.Result;
import com.longmaster.platform.service.NewsInfoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/manage/newsInfo")
@Api(value = "NewsInfoController", tags = "新闻")
public class NewsInfoController {

    @Resource
    private NewsInfoService newsInfoService;


    @GetMapping("queryList")
    public Result queryList(Integer page, Integer size) throws Exception {
        return Result.SUCCESS(newsInfoService.queryList(page, size));
    }

    @GetMapping("info")
    public Result info(String id) throws Exception {
        return Result.SUCCESS(newsInfoService.queryInfo(id));
    }

}
