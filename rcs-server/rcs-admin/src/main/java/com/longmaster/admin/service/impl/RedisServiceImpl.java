package com.longmaster.admin.service.impl;

import com.longmaster.admin.service.IRedisValueGetter;
import com.longmaster.admin.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class RedisServiceImpl implements RedisService {

    @Resource
    private RedisTemplate redisTemplate;

    private Map<String, ReentrantLock> mReentrantLockMap = new ConcurrentHashMap<>();

    @Override
    public boolean isExist(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public boolean setIfAbsent(String key, Object obj, Long expire) {
        boolean result = false;
        try {
            result = redisTemplate.opsForValue().setIfAbsent(key, obj, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public boolean set(String key, Object obj, Long expire) {
        boolean result = true;
        try {
            redisTemplate.opsForValue().set(key, obj, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    @Override
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    public <T> T unblockingGet(String key, Long expire, IRedisValueGetter<T> valueGetter) {
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
    public <T> T blockingGet(String key, Long expire, IRedisValueGetter<T> valueGetter) throws InterruptedException {
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
