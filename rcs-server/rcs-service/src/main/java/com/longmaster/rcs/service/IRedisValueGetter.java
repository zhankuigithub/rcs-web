package com.longmaster.rcs.service;

public interface IRedisValueGetter<T> {
    T getValueIfCacheMiss();
}
