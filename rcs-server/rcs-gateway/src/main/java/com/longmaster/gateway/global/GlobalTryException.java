package com.longmaster.gateway.global;

import cn.hutool.core.bean.BeanUtil;
import com.longmaster.gateway.exception.Result;
import com.longmaster.gateway.exception.ServerException;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.server.*;

import java.util.Map;

public class GlobalTryException extends DefaultErrorWebExceptionHandler {

    public GlobalTryException(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = super.getError(request);

        if (error instanceof ServerException) {
            return BeanUtil.beanToMap(((ServerException) error).getResult());
        }

        error.printStackTrace();
        return BeanUtil.beanToMap(Result.ERROR(null));
    }

    // 指定响应处理方法为JSON处理的方法
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return 200;
    }
}

