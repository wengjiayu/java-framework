package com.fatiger.framework.rest.config;

import condition.SwaggerCondition;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author wengjiayu
 */
@EnableSwagger2
@Conditional(SwaggerCondition.class)
public class SwaggerConfig {

    @Bean
    @Lazy
    public Docket createRestApi(@Value("${swagger.title}") String title, @Value("${swagger.contact}") String contact,
                                @Value("${swagger.version}") String version, @Value("${swagger.basePackage}") String basePackage) {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo(title, contact, version)).select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class)).paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo(String title, String contact, String version) {
        return new ApiInfoBuilder().title(title).contact(new Contact(contact, "", "")).version(version).build();
    }
}

