package com.longmaster.platform.service.impl;

import com.longmaster.platform.cache.CacheHelper;
import com.longmaster.platform.cache.GlobalCacheKeyDefine;
import com.longmaster.platform.dto.chatbot.ChatBotDeveloperDTO;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.ChatbotDeveloper;
import com.longmaster.platform.mapper.ChatbotDeveloperMapper;
import com.longmaster.platform.mapper.ChatbotMapper;
import com.longmaster.platform.service.ChatbotDeveloperService;
import com.longmaster.platform.service.RedisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 机器人开发者信息 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Service
public class ChatbotDeveloperServiceImpl extends ServiceImpl<ChatbotDeveloperMapper, ChatbotDeveloper> implements ChatbotDeveloperService {

    @Resource
    private ChatbotMapper chatbotMapper;

    @Resource
    private RedisService redisService;

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public JsonNode getOneChatBotDeveloper(String chatBotId) {
        String key = CacheHelper.buildChatBotCacheKey(GlobalCacheKeyDefine.CHAT_BOT_DEVELOPER, chatBotId);

        return redisService.unblockingGet(key, 60 * 60L, () -> {
            Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, chatBotId).select(Chatbot::getId, Chatbot::getCarrierId));

            if (chatbot == null) {
                return null;
            }
            ObjectNode objectNode = sObjectMapper.createObjectNode();
            ChatbotDeveloper chatbotDeveloper = this.getOne(new LambdaQueryWrapper<ChatbotDeveloper>()
                    .eq(ChatbotDeveloper::getChatBotId, chatbot.getId()).select(ChatbotDeveloper::getAppId, ChatbotDeveloper::getAppKey));

            if (chatbotDeveloper == null) {
                return null;
            }

            objectNode.put("appId", chatbotDeveloper.getAppId());
            objectNode.put("appKey", chatbotDeveloper.getAppKey());
            objectNode.put("carrierId", chatbot.getCarrierId());
            return objectNode;
        });
    }

    @Override
    public ChatBotDeveloperDTO getDeveloperConfig(Long chatBotId) {
        return baseMapper.selectDeveloperConfig(chatBotId);
    }
}
