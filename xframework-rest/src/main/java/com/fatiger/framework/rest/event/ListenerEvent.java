package com.fatiger.framework.rest.event;

import com.fatiger.framework.core.context.BaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Created by wengjiayu on 02/11/2017.
 * E-mail wengjiayu521@163.com
 */
@Slf4j
public class ListenerEvent implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationStartedEvent) {
        SpringApplication application = applicationStartedEvent.getSpringApplication();
        application.setBannerMode(Banner.Mode.CONSOLE);
        BaseProperties.loadData(applicationStartedEvent.getEnvironment());
    }
}
