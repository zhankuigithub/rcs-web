package com.longmaster.core.handler;

import com.longmaster.core.bean.annotation.RcsAction;
import com.longmaster.core.bean.annotation.RcsScene;
import com.longmaster.core.clazz.MethodClass;
import com.longmaster.core.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author zk
 * date 2021/4/16 9:24
 * description rcs注册场景的基本类
 */
public abstract class BaseRcsHandlerConfig {

    protected Map<String, MethodClass> methodMap;

    protected abstract String getPkg();

    protected BaseRcsHandlerConfig() {
        reload();
    }

    public MethodClass getClazzByMethod(String method) {
        if (methodMap.isEmpty()) {
            synchronized (this) {
                if (methodMap.isEmpty()) {
                    reload();
                }
            }
        }

        if (methodMap.containsKey(method)) {
            return methodMap.get(method);
        }
        return null;
    }

    protected void reload() {

        List<Class<?>> clazzs = ReflectionUtil.getClasses(getPkg());

        if (clazzs != null && clazzs.size() > 0) {

            Map<String, MethodClass> map = new HashMap<>();
            for (Class cls : clazzs) {

                boolean annotation = cls.isAnnotationPresent(RcsScene.class);
                if (annotation) {

                    Method[] methods = cls.getDeclaredMethods();
                    for (Method method : methods) {

                        boolean present = method.isAnnotationPresent(RcsAction.class);
                        if (present) {

                            RcsAction methodAnnotation = method.getAnnotation(RcsAction.class);
                            List<String> list = Arrays.asList(methodAnnotation.value());
                            if (list != null && list.size() > 0) {
                                list.forEach(key -> map.put(key, new MethodClass(method, cls)));
                            }
                        }
                    }
                }
            }
            methodMap = map;
        } else {
            methodMap = new HashMap<>();
        }
    }
}
