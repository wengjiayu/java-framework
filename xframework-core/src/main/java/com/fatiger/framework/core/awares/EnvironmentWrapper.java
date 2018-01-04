package com.fatiger.framework.core.awares;

import com.fatiger.framework.core.context.BaseProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by wengjiayu on 31/10/2017.
 * E-mail wengjiayu521@163.com
 */

@Configuration
public class EnvironmentWrapper implements EnvironmentAware {
    @Override
    public void setEnvironment(Environment event) {
//        BaseProperties.loadData(event);
        BaseProperties.loadData(event);
    }
//    @Bean
//    @Conditional(SpringContextCondition.class)
//    public SpringApplicationContext springApplicationContext(ApplicationContext applicationContext) {
//        SpringApplicationContext context = new SpringApplicationContext();
//        context.setApplicationContext(applicationContext);
//        return context;
//    }


}
