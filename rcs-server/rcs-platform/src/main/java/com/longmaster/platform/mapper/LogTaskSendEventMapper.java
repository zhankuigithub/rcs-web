package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.dto.group.LogTaskSendEventDTO;
import com.longmaster.platform.dto.statistical.MessageEventDTO;
import com.longmaster.platform.dto.statistical.SearchDTO;
import com.longmaster.platform.entity.LogTaskSendEvent;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface LogTaskSendEventMapper extends BaseMapper<LogTaskSendEvent> {

    IPage<LogTaskSendEvent> pageSelect(IPage<MessageEventDTO> page, @Param("ew") MessageEventDTO params);

    /**
     * 详细数据
     *
     * @param page
     * @param params
     * @return
     */
    IPage<LogTaskSendEventDTO> selectItems(IPage<SearchDTO> page, @Param("ew") SearchDTO params);


    /**
     * 修改状态
     *
     * @param messageId
     * @param chatBotId
     * @param phoneNum
     * @param status
     */
    void updateStatus(@Param("messageId") String messageId, @Param("chatBotId") String chatBotId, @Param("phoneNum") String phoneNum, @Param("status") Integer status);


    /**
     * 按照任务id查详情
     *
     * @param page
     * @param params
     * @return
     */
    IPage<Map> detailPage(IPage<MessageEventDTO> page, @Param("params") Map params);
}
