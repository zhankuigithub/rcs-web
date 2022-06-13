package com.longmaster.rcs.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
@Slf4j
public class HttpInterceptor implements ClientHttpRequestInterceptor {


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) {
        HttpHeaders headers = request.getHeaders();
        log.info("-----------------------------------------------------------------------------------------------------------");
        log.info("请求地址 : {}", request.getURI());
        log.info("请求方法 : {}", request.getMethod());
        log.info("请求头   : {}", headers);
        log.info("请求体   : {}", new String(body, StandardCharsets.UTF_8));
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        HttpHeaders headers = response.getHeaders();
        log.info("响应码   : {}", response.getStatusCode());
        log.info("响应头   : {}", headers);

        List<String> list = headers.get(HttpHeaders.CONTENT_DISPOSITION);
        if (list != null && list.get(0) != null) {
            log.info("响应体   : {}", list.get(0));
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
                line = bufferedReader.readLine();
            }
        }
        log.info("响应体   : {}", stringBuilder);
    }
}

