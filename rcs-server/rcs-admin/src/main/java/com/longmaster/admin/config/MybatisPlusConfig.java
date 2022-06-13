package com.longmaster.admin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.longmaster.core.handler.MybatisPlusFillHandler;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Lazy
@Configuration
@Import(MybatisPlusFillHandler.class)
@MapperScan("com.longmaster.admin.mapper")
public class MybatisPlusConfig {

    public MybatisPlusConfig() {

    }

    /*nacos切换数据库配置，可自动刷新*/
    @Bean
    @RefreshScope
    @ConfigurationProperties(prefix = "spring.datasource")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @RefreshScope
    public HikariDataSource hikariDataSource(HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }


    @Bean
    public IKeyGenerator keyGenerator() {
        return new H2KeyGenerator();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
