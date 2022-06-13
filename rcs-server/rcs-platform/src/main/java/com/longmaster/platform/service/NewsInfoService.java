package com.longmaster.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.platform.entity.NewsInfo;

import java.util.Map;

public interface NewsInfoService extends IService<NewsInfo> {

    Map<String, Object> queryList(int page, int size) throws Exception;

    Map<String, Object> queryInfo(String id) throws Exception;
}
