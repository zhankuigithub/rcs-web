package com.longmaster.admin.config;

import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import javax.annotation.Resource;


@Lazy
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfiguration {
    @Resource
    protected Environment env;

    @Resource
    protected OpenApiExtensionResolver openApiExtensionResolver;

    @Bean("defaultApi")
    public Docket defaultApi() {
        String host = env.getProperty("knife4j.config.host", "");
        String groupName = env.getProperty("knife4j.config.groupName", "default");
        String scanPath = env.getProperty("knife4j.config.scanPackage", "");

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .host(host)
                .apiInfo(apiInfo(host))
                .groupName(groupName)
                .select()
                .apis(RequestHandlerSelectors.basePackage(scanPath))
                .paths(PathSelectors.any())
                .build()
                .extensions(openApiExtensionResolver.buildExtensions(groupName));
        return docket;
    }


    protected ApiInfo apiInfo(String host) {
        return new ApiInfoBuilder()
                .title(env.getProperty("knife4j.config.title", "接口文档"))
                .description(env.getProperty("knife4j.config.title", "接口文档"))
                .termsOfServiceUrl(host)
                .version(env.getProperty("knife4j.config.version", "1.0"))
                .contact(new Contact(
                                env.getProperty("knife4j.author.name", ""),
                                env.getProperty("knife4j.author.url", ""),
                                env.getProperty("knife4j.author.email", "")
                        )
                )
                .build();
    }
}
