package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.dto.statistical.SearchDTO;
import com.longmaster.platform.dto.statistical.TemplateEventItemsDTO;
import com.longmaster.platform.entity.LogMessageTemplateEvent;
import org.apache.ibatis.annotations.Param;

public interface LogMessageTemplateEventMapper extends BaseMapper<LogMessageTemplateEvent> {

    /**
     * 消息模板点击统计-详细数据
     * @param page
     * @param params
     * @return
     */
    IPage<TemplateEventItemsDTO> selectMessageTemplateEventItems(IPage<SearchDTO> page, @Param("ew") SearchDTO params);

}
