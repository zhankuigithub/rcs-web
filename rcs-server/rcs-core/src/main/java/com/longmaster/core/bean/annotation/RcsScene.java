package com.longmaster.core.bean.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RcsScene {

    String value() default "";

}
