package com.longmaster.admin.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class HttpInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        log.info("请求地址：{}", request.getURI());
        log.info("请求方法：{}", request.getMethod());
        log.info("请求内容：{}", new String(body));
        log.info("请求头：{}", request.getHeaders());
        return execution.execute(request, body);
    }
}
