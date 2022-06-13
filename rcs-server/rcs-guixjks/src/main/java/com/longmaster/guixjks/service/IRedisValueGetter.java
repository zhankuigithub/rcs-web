package com.longmaster.guixjks.service;

public interface IRedisValueGetter<T> {
    T getValueIfCacheMiss();
}
