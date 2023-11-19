package com.example.simplesms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class MultipartConfig {

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setResolveLazily(true);
        multipartResolver.setMaxUploadSize(1024L * 1024L * 10L);
        multipartResolver.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        return multipartResolver;
    }
}
