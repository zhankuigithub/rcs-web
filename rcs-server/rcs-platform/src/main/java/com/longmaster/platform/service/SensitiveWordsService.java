package com.longmaster.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.platform.dto.sensitiveWords.WordInfoDTO;
import com.longmaster.platform.entity.SensitiveWords;

import java.util.List;


public interface SensitiveWordsService extends IService<SensitiveWords> {

    PageResult<SensitiveWords> pageQuery(PageParam<SensitiveWords> pageParam);

    boolean add(WordInfoDTO wordInfo);

    boolean update(SensitiveWords sensitiveWords);

    boolean delete(List<Integer> ids);

    List<String> check(String content);
}
