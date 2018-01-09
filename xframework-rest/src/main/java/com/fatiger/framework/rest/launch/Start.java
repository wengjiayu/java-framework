package com.fatiger.framework.rest.launch;

import com.fatiger.framework.rest.event.ListenerEvent;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.ClassPathResource;

/**
 * @author wengjiayu
 * @date 15/12/2017
 * @E-mail wengjiayu521@163.com
 */
public class Start {
    private Start() {
    }

    public static void run(Class<?> clazz, String[] args) {
        String utf8CharsetName = "UTF-8";
        System.setProperty("file.encoding", utf8CharsetName);
        System.setProperty("sun.jnu.encoding", utf8CharsetName);
        System.setProperty("sun.zip.encoding", utf8CharsetName);
        new SpringApplicationBuilder().listeners(new ListenerEvent()).sources(clazz).banner(new ResourceBanner(new ClassPathResource("banner.txt"))).run(args);
    }
}
