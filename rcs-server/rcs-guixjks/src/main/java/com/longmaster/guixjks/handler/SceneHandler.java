package com.longmaster.guixjks.handler;

import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.clazz.MethodClass;
import com.longmaster.guixjks.Constants;
import com.longmaster.guixjks.config.BeanConfig;
import com.longmaster.guixjks.config.RcsHandlerConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;


/**
 * @Author yzq
 * @Date 2020/8/12 10:34
 * @Version 1.0
 * @description:场景处理主入口
 */
@Component
public class SceneHandler {

    @Async
    public void start(MaapMessage maapMessage) throws IllegalAccessException, InvocationTargetException {
        resolve(maapMessage);
        MethodClass methodClass = RcsHandlerConfig.getInstance().getClazzByMethod(maapMessage.getScene());
        if (methodClass == null) {
            return;
        }
        // 获取方法和class的实例对象
        methodClass.getMethod().invoke(BeanConfig.getBean(methodClass.getClazz()), maapMessage);
    }

    /**
     * 处理智能导诊动态点击
     */
    private void resolve(MaapMessage maapMessage) {
        // 默认使用文案

        if (maapMessage.getScene().contains(Constants.ToSeeDoctor.EN_SEE_DOCTOR)) {
            // 动态问诊的
            if (maapMessage.getScene().contains(Constants.ToSeeDoctor.Dynamic.SBC_MENU_GUIDANCE_CHAT)) {
                maapMessage.setScene(Constants.ToSeeDoctor.Dynamic.SBC_MENU_GUIDANCE_CHAT);
            }

            // 动态点击的
            if (maapMessage.getScene().contains(Constants.ToSeeDoctor.Dynamic.SBC_MENU_GUIDANCE_ASK)) {
                maapMessage.setScene(Constants.ToSeeDoctor.Dynamic.SBC_MENU_GUIDANCE_ASK);
            }
        }
    }

}
