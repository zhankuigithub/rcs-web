package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.platform.dto.group.LogTaskSendEventDTO;
import com.longmaster.platform.dto.message.MaapMessage;
import com.longmaster.platform.dto.receive.LogReceiveMessageDTO;
import com.longmaster.platform.dto.statistical.*;
import com.longmaster.platform.entity.LogSceneMessage;

import java.util.Map;

public interface StatisticalService {

    /**
     * 获取首页数据
     *
     * @return
     */
    Map<String, Object> getHomeStatistical(Long customerId);


    /**
     * 记录群发事件
     *
     * @param messageId
     * @param name
     * @param chatBotId
     * @param phoneNum
     * @param carrierId
     * @param messageTemplateId
     */
    void saveGroupEventMessage(Long taskId, String messageId, String name,
                               String chatBotId, Long appId,
                               String phoneNum, Long carrierId,
                               Long messageTemplateId, String messageTemplateName,
                               Integer messageTemplateType, Integer messageTemplateBackType, Integer status);


    /**
     * 群-汇总
     *
     * @param groupEventStat
     * @return
     */
    SataDTO getGroupEventStat(EventStatDTO groupEventStat);


    /**
     * 菜单-汇总
     *
     * @param groupEventStat
     * @return
     */
    SataDTO getMenuEventStat(EventStatDTO groupEventStat);

    /**
     * 菜单- 详细数据
     *
     * @return
     */
    JsonNode getAllMenuItems(MenuItemsSearchDTO menuItemsSearch);

    /**
     * 消息模板-汇总
     *
     * @param groupEventStat
     * @return
     */
    SataDTO getMessageTemplateEventStat(EventStatDTO groupEventStat);


    /**
     * 上行消息-汇总
     *
     * @param groupEventStat
     * @return
     */
    SataDTO getReceiveEventStat(EventStatDTO groupEventStat);

    /**
     * 会话交互-汇总
     *
     * @param groupEventStat
     * @return
     */
    SataDTO getSceneEventStat(EventStatDTO groupEventStat);


    /**
     * 群-详细数据
     *
     * @param pageParam
     * @return
     */
    IPage<LogTaskSendEventDTO> groupItems(PageParam<SearchDTO> pageParam);


    /**
     * 消息模板-详细数据
     *
     * @param pageParam
     * @return
     */
    IPage<TemplateEventItemsDTO> messageTemplateEventItems(PageParam<SearchDTO> pageParam);


    /**
     * 上行消息统计
     *
     * @param pageParam
     * @return
     */
    IPage<LogReceiveMessageDTO> logReceiveMessageItems(PageParam<SearchUpDTO> pageParam);


    /**
     * 会话交互-详细数据
     *
     * @param pageParam
     * @return
     */
    IPage<LogSceneMessage> logSceneMessageItems(PageParam<SearchUpDTO> pageParam);


    /**
     * 保存上行消息
     *
     * @param maapMessage
     * @throws JsonProcessingException
     */
    void saveReceiveMessage(MaapMessage maapMessage);

    /**
     * 收发记录
     * @param pageParam
     * @return
     */
    PageResult<Map<String, String>> selectSendAndReceiveLog(PageParam<Map<String, Object>> pageParam);

    /**
     * 对话记录
     * @param pageParam
     * @return
     */
    PageResult<Map<String, String>> selectChatLog(PageParam<Map<String, Object>> pageParam);

}
