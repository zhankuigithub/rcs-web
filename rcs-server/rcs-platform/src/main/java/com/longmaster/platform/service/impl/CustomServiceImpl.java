package com.longmaster.platform.service.impl;


import com.longmaster.platform.cache.CacheHelper;
import com.longmaster.platform.cache.GlobalCacheKeyDefine;
import com.longmaster.platform.dto.message.MaapMessage;
import com.longmaster.platform.entity.Application;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.mapper.ApplicationMapper;
import com.longmaster.platform.mapper.ChatbotMapper;
import com.longmaster.platform.service.CustomService;
import com.longmaster.platform.service.RedisService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * author zk
 * date 2021/3/15 15:29
 * description 处理找不到关键词的特殊回调service
 */
@Component
public class CustomServiceImpl implements CustomService {

    @Resource
    private RestTemplate balancedRestTemplate;

    @Resource
    private ChatbotMapper chatbotMapper;

    @Resource
    private ApplicationMapper applicationMapper;

    @Resource
    private RedisService redisService;

    @Override
    public void sendToCustom(MaapMessage message) {
        String chatBotId = message.getDestinationAddress();
        String key = CacheHelper.buildChatBotCacheKey(GlobalCacheKeyDefine.CHAT_BOT_CALLBACK_URL, chatBotId);

        String callbackUrl = redisService.unblockingGet(key, 60 * 60L, () -> {
            Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, chatBotId));
            if (chatbot != null) {
                Long appId = chatbot.getAppId();
                Application application = applicationMapper.selectOne(new LambdaQueryWrapper<Application>().eq(Application::getId, appId));
                if (application != null) {
                    return application.getCallbackUrl();
                }
            }
            return null;
        });

        if (!StrUtil.isEmpty(callbackUrl)) {
            HttpEntity httpEntity = new HttpEntity(message);
            balancedRestTemplate.exchange(callbackUrl, HttpMethod.POST, httpEntity, String.class);
        }
    }
}
