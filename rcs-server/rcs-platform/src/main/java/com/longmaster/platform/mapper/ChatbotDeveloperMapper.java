package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.chatbot.ChatBotDeveloperDTO;
import com.longmaster.platform.entity.ChatbotDeveloper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 机器人开发者信息 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface ChatbotDeveloperMapper extends BaseMapper<ChatbotDeveloper> {

    ChatBotDeveloperDTO selectDeveloperConfig(Long chatBotId);
}
