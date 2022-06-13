package com.longmaster.platform.service;

import com.longmaster.platform.dto.chatbot.ChatBotDeveloperDTO;
import com.longmaster.platform.entity.ChatbotDeveloper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>
 * 机器人开发者信息 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface ChatbotDeveloperService extends IService<ChatbotDeveloper> {


    JsonNode getOneChatBotDeveloper(String chatBotId);

    ChatBotDeveloperDTO getDeveloperConfig(Long chatBotId);

}
