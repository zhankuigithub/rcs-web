package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longmaster.platform.entity.NewsInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewsInfoMapper extends BaseMapper<NewsInfo> {

    List<NewsInfo> queryList(@Param("index") int index, @Param("size") int size);


    NewsInfo selectBefore(@Param("weight") int weight);


    NewsInfo selectAfter(@Param("weight") int weight);

}
