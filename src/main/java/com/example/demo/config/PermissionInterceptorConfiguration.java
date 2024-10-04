package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Bean
    PermissionInerceptor getPermissionInterceptor() {
        return new PermissionInerceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whileList={
                "/", "/api/v1/auth/**","/storage/**"
                ,"/api/v1/companies/**","/api/v1/skills","/api/v1/jobs/**","/api/v1/files"
        };
        registry.addInterceptor(getPermissionInterceptor()).excludePathPatterns(whileList);
    }
}

