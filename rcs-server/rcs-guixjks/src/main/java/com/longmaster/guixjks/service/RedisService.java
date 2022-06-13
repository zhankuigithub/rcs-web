package com.longmaster.guixjks.service;

/**
 * author zk
 * date 2021/3/10 9:04
 * description redis操作类
 */
public interface RedisService {

    boolean set(String key, Object obj, int expire);

    <T> T get(String key);

    <T> T unblockingGet(String key, int expire, IRedisValueGetter<T> var3);

    <T> T blockingGet(String key, int expire, IRedisValueGetter<T> var3) throws InterruptedException;

}
