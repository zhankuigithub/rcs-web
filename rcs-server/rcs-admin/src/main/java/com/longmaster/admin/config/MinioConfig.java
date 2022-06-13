package com.longmaster.admin.config;

import cn.hutool.core.util.URLUtil;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Slf4j
@Configuration
public class MinioConfig {

    @Resource
    private Environment env;

    @Lazy
    @Bean
    public MinioClient minioClient() {
        log.info("[MinioClient] Minio Client start init...");
        MinioClient minioClient = MinioClient.builder()
                .endpoint(URLUtil.url(env.getProperty("system.minio.endpoint", "")))
                .credentials(
                        env.getProperty("system.minio.accessKey", ""),
                        env.getProperty("system.minio.secretKey", "")
                )
                .build();

        return minioClient;
    }


}
