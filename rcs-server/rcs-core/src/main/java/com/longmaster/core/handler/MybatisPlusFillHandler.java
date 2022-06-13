package com.longmaster.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Slf4j
@Lazy
@Component
public class MybatisPlusFillHandler implements MetaObjectHandler {

    private static final String TAG = "MybatisPlusFillHandler";

    public MybatisPlusFillHandler() {
        log.info("[{}] init Mybatis-Plus fill handler by Spring SPI...", TAG);
    }

    @Override
    public void insertFill(MetaObject metaObject) {
    }

    @Override
    public void updateFill(MetaObject metaObject) {
    }
}
