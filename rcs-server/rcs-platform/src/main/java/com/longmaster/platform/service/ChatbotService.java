package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.chatbot.ChatBotDTO;
import com.longmaster.platform.dto.chatbot.ChatBotWrapperDTO;
import com.longmaster.platform.entity.Chatbot;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 机器人信息表 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface ChatbotService extends IService<Chatbot> {

    /**
     * 分页查询 机器人列表  包含 客户、运营商信息
     *
     * @param pageParam 分页参数
     * @return
     */
    IPage<ChatBotDTO> pageSelect(PageParam<ChatBotDTO> pageParam, String carrierIds);

    /**
     * addCtChatbot
     *
     * @param wrapper
     */
    void saveChatBot(ChatBotWrapperDTO wrapper);

    /**
     * 获取机器信息
     *
     * @param id 机器人ID
     * @return
     */
    ChatBotWrapperDTO selectDetail(Long id);


    /**
     * 查询运营商机器详情
     *
     * @param chatBotId
     * @return
     */
    ChatBotWrapperDTO selectDetail(String chatBotId);


    /**
     * 上下线
     *
     * @param id
     * @return
     */
    boolean onlineChatbot(String id);

    /**
     * 新建机器人
     *
     * @param wrapper
     * @return
     */
    boolean createChatBot(ChatBotWrapperDTO wrapper);

    /**
     * 修改机器人
     *
     * @param wrapper
     * @return
     */
    boolean editChatBot(ChatBotWrapperDTO wrapper);


    /**
     * 删除机器人之前发送验证码
     */
    void sendCode(String token, String sessionId) throws JsonProcessingException;

    /**
     * 删除机器人
     *
     * @param id
     * @return
     */
    boolean deleteChatBot(Long id, String code, String sessionId);

    /**
     * 通过cid获取chatbot
     *
     * @param customerId
     * @return
     */
    List<Map<String, Object>> selectChatBotByCid(String customerId);
}
