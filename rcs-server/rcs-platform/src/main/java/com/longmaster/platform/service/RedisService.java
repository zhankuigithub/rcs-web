package com.longmaster.platform.service;

/**
 * author zk
 * date 2021/3/10 9:04
 * description redis操作类
 */
public interface RedisService {

    boolean expire(String key, Long expire);

    Long increment(String key);

    boolean isExist(String key);

    boolean setIfAbsent(String key, Object obj, Long expire);

    boolean set(String key, Object obj, Long expire);

    boolean delete(String key);

    <T> T get(String key);

    <T> T unblockingGet(String key, Long expire, IRedisValueGetter<T> var3);

    <T> T blockingGet(String key, Long expire, IRedisValueGetter<T> var3) throws InterruptedException;

}
