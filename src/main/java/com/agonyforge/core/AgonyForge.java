package com.agonyforge.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import static java.util.Collections.singletonList;

@SpringBootApplication
public class AgonyForge {
    public AgonyForge(FreeMarkerConfigurer freeMarkerConfigurer) {
        freeMarkerConfigurer.getTaglibFactory().setClasspathTlds(singletonList("/META-INF/security.tld"));
        freeMarkerConfigurer.getTaglibFactory().setObjectWrapper(freeMarkerConfigurer.getConfiguration().getObjectWrapper());
    }
    public static void main(String ... args) {
        SpringApplication.run(AgonyForge.class, args);
    }
}
