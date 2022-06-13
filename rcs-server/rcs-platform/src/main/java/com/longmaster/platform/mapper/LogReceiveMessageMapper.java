package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.dto.receive.LogReceiveMessageDTO;
import com.longmaster.platform.dto.statistical.SearchUpDTO;
import com.longmaster.platform.dto.statistical.StatisticalDTO;
import com.longmaster.platform.entity.LogReceiveMessage;
import com.longmaster.platform.entity.MenuItemClickEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LogReceiveMessageMapper extends BaseMapper<LogReceiveMessage> {

    IPage<LogReceiveMessage> pageSelect(IPage<StatisticalDTO> page, @Param("ew") StatisticalDTO params);

    /**
     * 上行消息详细数据
     * @param page
     * @param params
     * @return
     */
    IPage<LogReceiveMessageDTO> selectReceiveEventItems(IPage<SearchUpDTO> page, @Param("ew") SearchUpDTO params);

    /**
     * 菜单点击统计
     *
     * @return
     */
    List<MenuItemClickEntity> selectAllMenuItemClickItems(@Param("appId") Long appId, @Param("carrierId") Long carrierId);
}
