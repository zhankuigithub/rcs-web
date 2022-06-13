package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.entity.SensitiveWords;
import org.apache.ibatis.annotations.Param;


public interface SensitiveWordsMapper extends BaseMapper<SensitiveWords> {

    IPage<SensitiveWords> pageSelect(IPage<SensitiveWords> page, @Param("params") SensitiveWords params);

}
