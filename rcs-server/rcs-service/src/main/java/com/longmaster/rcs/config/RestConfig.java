package com.longmaster.rcs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.rcs.filter.HttpInterceptor;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestConfig {

    private static final int HTTP_CLIENT_RETRY_COUNT = 3;

    private static final int MAXIMUM_TOTAL_CONNECTION = 10;

    private static final int MAXIMUM_CONNECTION_PER_ROUTE = 5;

    private static final int CONNECTION_VALIDATE_AFTER_INACTIVITY_MS = 10 * 1000;

    /**
     * @param connectionTimeoutMs milliseconds/毫秒
     * @param readTimeoutMs       milliseconds/毫秒
     * @return
     */
    public static RestTemplate createRestTemplate(int connectionTimeoutMs, int readTimeoutMs, ObjectMapper objectMapper) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpClientBuilder clientBuilder = HttpClients.custom();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        // 整个连接池最大连接数
        connectionManager.setMaxTotal(MAXIMUM_TOTAL_CONNECTION);
        // 设置每个路由的最大并发连接数,默认为2.
        connectionManager.setDefaultMaxPerRoute(MAXIMUM_CONNECTION_PER_ROUTE);
        // 官方推荐使用检查永久链接的可用性,而不推荐每次请求的时候才去检查 (milliseconds 毫秒)
        connectionManager.setValidateAfterInactivity(CONNECTION_VALIDATE_AFTER_INACTIVITY_MS);

        clientBuilder.setConnectionManager(connectionManager);
        //设置重连操作次数，这里设置了3次
        clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(HTTP_CLIENT_RETRY_COUNT, true, new ArrayList<>()) {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                HttpRequestWrapper httpRequestWrapper = (HttpRequestWrapper) context.getAttribute("http.request");
                return super.retryRequest(exception, executionCount, context);
            }
        });

        //使用httpClient创建一个ClientHttpRequestFactory的实现
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(clientBuilder.build());
        httpRequestFactory.setConnectTimeout(connectionTimeoutMs);
        httpRequestFactory.setConnectionRequestTimeout(readTimeoutMs);
        httpRequestFactory.setReadTimeout(readTimeoutMs);
        // 支持https
        httpRequestFactory.setHttpClient(sslClient());

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        // 添加自定义拦截器
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HttpInterceptor());
        restTemplate.setInterceptors(interceptors);
        //提供对传出/传入流的缓冲,可以让响应body多次读取(如果不配置,拦截器读取了Response流,再响应数据时会返回body=null)
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpRequestFactory));

        MappingJackson2HttpMessageConverter messageConverter = restTemplate.getMessageConverters().stream().filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast).findFirst().orElseThrow(() -> new RuntimeException("MappingJackson2HttpMessageConverter not found"));
        messageConverter.setObjectMapper(objectMapper);

        //防止响应中文乱码
        restTemplate.getMessageConverters().stream().filter(StringHttpMessageConverter.class::isInstance).map(StringHttpMessageConverter.class::cast).forEach(a -> {
            a.setWriteAcceptCharset(false);
            a.setDefaultCharset(StandardCharsets.UTF_8);
        });

        return restTemplate;
    }

    @Bean
    public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestTemplate restTemplate = RestConfig.createRestTemplate(20000, 20000, new ObjectMapper());
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });
        return restTemplate;
    }

    @Bean("balancedRestTemplate")
    @LoadBalanced
    public RestTemplate balancedRestTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestTemplate restTemplate = RestConfig.createRestTemplate(20000, 20000, new ObjectMapper());
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });
        return restTemplate;
    }


    @Bean("cspRestTemplate")
    public RestTemplate cspRestTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestTemplate restTemplate = new RestTemplate(configSSL());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringHttpMessageConverter.setWriteAcceptCharset(true);
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.ALL);
        for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
            if (restTemplate.getMessageConverters().get(i) instanceof StringHttpMessageConverter) {
                restTemplate.getMessageConverters().remove(i);
                restTemplate.getMessageConverters().add(i, stringHttpMessageConverter);
            }
            if (restTemplate.getMessageConverters().get(i) instanceof MappingJackson2HttpMessageConverter) {
                try {
                    ((MappingJackson2HttpMessageConverter) restTemplate.getMessageConverters().get(i)).setSupportedMediaTypes(mediaTypeList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return restTemplate;
    }

    /**
     * 支持https
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws KeyStoreException
     */
    private HttpComponentsClientHttpRequestFactory configSSL() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(sslClient());
        return requestFactory;
    }

    private static CloseableHttpClient sslClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();
        return httpClient;
    }

}
