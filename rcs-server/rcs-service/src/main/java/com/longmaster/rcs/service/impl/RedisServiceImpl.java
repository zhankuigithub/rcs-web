package com.longmaster.rcs.service.impl;

import com.longmaster.rcs.service.IRedisService;
import com.longmaster.rcs.service.IRedisValueGetter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class RedisServiceImpl implements IRedisService {

    @Resource
    private RedisTemplate redisTemplate;

    private Map<String, ReentrantLock> mReentrantLockMap = new ConcurrentHashMap<>();

    @Override
    public boolean set(String key, Object obj, int expire) {
        boolean result = false;
        try {
            result = redisTemplate.opsForValue().setIfAbsent(key, obj, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    public <T> T unblockingGet(String key, int expire, IRedisValueGetter<T> valueGetter) {
        T value = get(key);
        if (value != null) {
            return value;
        }
        value = valueGetter.getValueIfCacheMiss();
        if (value != null) {
            set(key, value, expire);
        }
        return value;
    }

    @Override
    public <T> T blockingGet(String key, int expire, IRedisValueGetter<T> valueGetter) throws InterruptedException {
        T value = get(key);
        if (value != null) {
            return value;
        }

        ReentrantLock keyLock = mReentrantLockMap.computeIfAbsent(key, k -> new ReentrantLock());
        if (keyLock.tryLock(1000, TimeUnit.MILLISECONDS)) {
            try {
                value = get(key);
                if (value == null) {
                    value = valueGetter.getValueIfCacheMiss();
                    if (value != null) {
                        set(key, value, expire);
                    }
                }
            } finally {
                keyLock.unlock();
                mReentrantLockMap.remove(key);
            }
        }
        return value;
    }
}
