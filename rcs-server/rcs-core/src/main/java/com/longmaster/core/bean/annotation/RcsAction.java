package com.longmaster.core.bean.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RcsAction {

    String[] value() default {};

}
