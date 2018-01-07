package com.fatiger.framework.rest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author wengjiayu
 * @date 25/12/2017
 * @E-mail wengjiayu521@163.com
 */
@Configuration
@ComponentScan(value = "com.fatiger.framework.rest")
@Slf4j
public class RestConfig {

    @PostConstruct
    public void init(){

        log.info("inited !!!!!!!!!!!!!!!");
    }

}
