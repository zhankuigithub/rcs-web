package com.longmaster.admin.service;

public interface IRedisValueGetter<T> {
    T getValueIfCacheMiss();
}
