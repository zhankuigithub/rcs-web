package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.dto.chatbot.ChatBotDTO;
import com.longmaster.platform.dto.chatbot.ChatBotWrapperDTO;
import com.longmaster.platform.entity.Chatbot;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 机器人信息表 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface ChatbotMapper extends BaseMapper<Chatbot> {

    /**
     * 分页查询 机器人列表  包含 客户、运营商信息
     *
     * @return
     */
    IPage<ChatBotDTO> pageSelect(IPage<ChatBotDTO> page, @Param("ew") ChatBotDTO params, @Param("carrierIds") String carrierIds);

    List<Chatbot> selectAllChatBotId(@Param("channelIds") List<Integer> channelIds, @Param("appId") Long appId);


    /**
     * 获取机器信息
     *
     * @param id 机器人Id
     * @return
     */
    ChatBotWrapperDTO queryDetail(@Param("id") Long id);


    ChatBotWrapperDTO queryDetailByChatBotId(@Param("chatBotId") String chatBotId);


    List<ChatBotDTO> selectChatbotList(@Param("appId") Long appId, @Param("carrierIds") String carrierIds);

    String getChatBotId(@Param("appName") String appName, @Param("customerName") String customerName, @Param("carrierId") int carrierId);


    List<Chatbot> getByCarrierIds(@Param("appId") Long appId, @Param("carrierIds") String carrierIds);


    List<String> getAllChatBotIdByCustomerId(@Param("customerId") Long customerId);


    List<Map<String, Object>> selectChatBotByCid(@Param("customerId") Long customerId);
}
