package com.mtxrii.file.mtxfile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Configuration
public class ConverterDumpConfig {

    @Bean
    public String dumpConverters(RequestMappingHandlerAdapter adapter) {
        System.out.println("=== HTTP Message Converters ===");
        for (HttpMessageConverter<?> c : adapter.getMessageConverters()) {
            System.out.println(c.getClass().getName());
        }
        return "converterDump";
    }
}