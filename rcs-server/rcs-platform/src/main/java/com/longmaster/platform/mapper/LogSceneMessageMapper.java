package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.dto.statistical.SearchUpDTO;
import com.longmaster.platform.entity.LogSceneMessage;
import org.apache.ibatis.annotations.Param;

public interface LogSceneMessageMapper extends BaseMapper<LogSceneMessage> {

    /**
     * 场景执行日志
     * @param page
     * @param params
     * @return
     */
    IPage<LogSceneMessage> selectSceneMessageItems(IPage<SearchUpDTO> page, @Param("ew") SearchUpDTO params);

}
