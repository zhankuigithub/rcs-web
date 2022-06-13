package com.longmaster.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.gateway.exception.Result;
import com.longmaster.gateway.exception.ServerException;
import com.longmaster.gateway.feign.AdminFeignService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

// 过滤器
@Component
@RefreshScope
public class GateWayFilter implements GlobalFilter, Ordered {

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

    @Resource
    private RedisTemplate redisTemplate;

    @Value("${system.whiteRoute}")
    private List<String> whiteRoute;

    @Resource
    private AdminFeignService adminFeignService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // 过滤页面或者白名单
        int isPage = String.valueOf(path).lastIndexOf(".");
        if (isPage > 0 || isWhiteUrl(path)) {
            return chain.filter(exchange);
        }
        HttpHeaders headers = request.getHeaders();

        List<String> list = headers.get("ACCESS-TOKEN");
        if (list == null || list.isEmpty()) {
            throw new ServerException("请提供有效凭证访问服务！(NO TOKEN)", Result.NO_TOKEN());
        }
        String accessToken = list.get(0);

        Object o = redisTemplate.opsForValue().get(accessToken);
        if (o == null) {
            throw new ServerException("登录过期，请重新登录！", Result.TOKEN_INVALID(null));
        }

        String carrierIds = adminFeignService.getCarrierIds(accessToken);
        request.mutate().header("TOKEN-CARRIER", carrierIds).build(); // 5g运营商权限
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isWhiteUrl(String path) {

        long count = whiteRoute.stream().filter(item -> {
            if (item.startsWith("/*") && item.endsWith("/*")) {
                String commonRoute = item.substring(item.indexOf("/*") + 2, item.lastIndexOf("/*"));
                return path.indexOf(commonRoute) >= 0;
            } else if (item.startsWith("/*")) {
                String commonRoute = item.substring(item.indexOf("/*") + 2);
                return path.endsWith(commonRoute);
            } else if (item.endsWith("/*")) {
                return path.startsWith(item.substring(0, item.lastIndexOf("/*")));
            } else {
                return item.equals(path);
            }
        }).count();

        return count > 0;
    }
}
