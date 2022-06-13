package com.longmaster.core.clazz;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Method;

@Setter
@Getter
@ToString
public class MethodClass {

    public MethodClass(Method method, Class<?> clazz) {
        this.method = method;
        this.clazz = clazz;
    }

    private Method method;
    private Class<?> clazz;
}
