package com.fatiger.framework.rest.launch;

import com.fatiger.framework.rest.event.ListenerEvent;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.ClassPathResource;

import static com.fatiger.framework.constant.General.DEFAULT_CHARSET;

/**
 * @author wengjiayu
 * @date 15/12/2017
 * @E-mail wengjiayu521@163.com
 */
public class Start {
    private Start() {
    }

    public static void main(String[] args) {
        System.setProperty("file.encoding", DEFAULT_CHARSET.name());
        System.setProperty("sun.jnu.encoding", DEFAULT_CHARSET.name());
        System.setProperty("sun.zip.encoding", DEFAULT_CHARSET.name());
        new SpringApplicationBuilder().listeners(new ListenerEvent()).sources(Run.class).banner(new ResourceBanner(new ClassPathResource("banner.txt"))).run(args);
    }
}
