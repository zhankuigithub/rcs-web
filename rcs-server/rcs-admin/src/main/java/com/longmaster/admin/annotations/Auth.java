package com.longmaster.admin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
    //是否加载当前访问用户会话信息
   boolean session() default true;

   //允许访问的角色列表
   String[] roles() default {};
}
