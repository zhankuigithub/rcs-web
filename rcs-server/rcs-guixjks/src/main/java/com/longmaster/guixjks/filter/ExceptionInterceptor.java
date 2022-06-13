package com.longmaster.guixjks.filter;

import com.longmaster.core.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class ExceptionInterceptor {


    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result errorHandler(HttpServletRequest request, Exception ex) {
        StringWriter errorStackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(errorStackTrace));
        // 将异常栈信息录入
        log.error(errorStackTrace.toString());
        return new Result(400, ex.toString());
    }

}
