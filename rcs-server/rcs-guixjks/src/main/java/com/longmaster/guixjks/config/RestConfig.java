package com.longmaster.guixjks.config;

import com.longmaster.guixjks.filter.HttpInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Configuration
public class RestConfig {

    @Lazy
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate(createHttpComponentsClientHttpRequestFactory());
        template.getInterceptors().add(new HttpInterceptor());
        template.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return template;
    }

    @Lazy
    @Bean("balancedRestTemplate")
    @LoadBalanced
    public RestTemplate platformRestTemplate() {
        RestTemplate template = new RestTemplate(createHttpComponentsClientHttpRequestFactory());
        template.getInterceptors().add(new HttpInterceptor());
        template.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return template;
    }

    private static HttpComponentsClientHttpRequestFactory createHttpComponentsClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(20000);
        httpRequestFactory.setConnectionRequestTimeout(20000);
        httpRequestFactory.setReadTimeout(20000);
        return httpRequestFactory;
    }

}
