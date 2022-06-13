package com.longmaster.core.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;
import java.util.Objects;



public class StrUtils {


    /**
     * 重构实体对象  字符串类型为空串时转为null值
     *
     * @param entity 实体对象
     * @return T
     */
    public static <T> T rebuildEntity(T entity) {
        try {
            if (Objects.isNull(entity)) {
                return null;
            }
            Field[] all = entity.getClass().getDeclaredFields();
            if (!entity.getClass().getSuperclass().equals(Object.class)){
                Field[] fields = entity.getClass().getSuperclass().getDeclaredFields();
                all = ArrayUtil.addAll(all, fields);
            }

            for (Field field : all) {
                field.setAccessible(true);
                Object str = field.get(entity);
                if (field.getType().equals(String.class) && StrUtil.isBlank(String.valueOf(str))) {
                    field.set(entity, null);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return entity;
    }
}
