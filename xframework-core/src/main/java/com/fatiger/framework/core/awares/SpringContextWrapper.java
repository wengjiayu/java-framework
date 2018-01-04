package com.fatiger.framework.core.awares;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Date;

/**
 * @author wengjiayu
 * @date 16/12/2017
 * @E-mail wengjiayu521@163.com
 */
@Configuration
public class SpringContextWrapper implements ApplicationContextAware {

    private static ApplicationContext ctx = null;

    public static Object getBean(String beanName) {
        return ctx.getBean(beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

}
