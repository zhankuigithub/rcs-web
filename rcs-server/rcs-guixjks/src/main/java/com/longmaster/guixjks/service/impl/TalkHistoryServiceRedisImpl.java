package com.longmaster.guixjks.service.impl;


import com.longmaster.guixjks.service.TalkHistoryService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class TalkHistoryServiceRedisImpl implements TalkHistoryService {

    private String PREFIX = "OP_ACTION_";

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public String queryTalkHistory(String phone) {
        return (String) redisTemplate.opsForValue().get(PREFIX + phone);
    }

    @Override
    public Boolean addTalk(String phone, String scene) {
        redisTemplate.opsForValue().set(PREFIX + phone, scene, 30, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public Boolean removeTalk(String phone) {
        redisTemplate.delete(PREFIX + phone);
        return true;
    }

}
