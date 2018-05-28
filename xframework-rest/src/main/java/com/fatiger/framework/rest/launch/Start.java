package com.fatiger.framework.rest.launch;

import com.fatiger.framework.rest.event.ListenerEvent;
import lombok.val;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.ClassPathResource;

import static com.fatiger.framework.constant.com.fatiger.framework.constant.dictionary.GeneralKey.DEFAULT_CHARSET;


/**
 * @author wengjiayu
 * @date 15/12/2017
 * @E-mail wengjiayu521@163.com
 */
public class Start {
    private Start() {
    }

    public static void run(Class<?> clazz, String[] args) {
        System.setProperty("file.encoding", DEFAULT_CHARSET.name());
        System.setProperty("sun.jnu.encoding", DEFAULT_CHARSET.name());
        System.setProperty("sun.zip.encoding", DEFAULT_CHARSET.name());
        final val applicationBuilder = new SpringApplicationBuilder();
        applicationBuilder.listeners(new ListenerEvent()).sources(clazz).banner(new ResourceBanner(new ClassPathResource("banner.txt")));
//        applicationBuilder.application().setWebApplicationType(WebApplicationType.REACTIVE);
        applicationBuilder.run(args);
    }
}
