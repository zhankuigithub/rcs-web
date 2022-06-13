package com.longmaster.platform.service;

public interface IRedisValueGetter<T> {
    T getValueIfCacheMiss();
}
