package com.example.demo.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfiguration {
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        //cho phep cac url nao co the ket noi toi backend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","http://localhost:4173","http://localhost:5173"));
        //cac method duoc ket noi
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        //cac phan header duoc phep gui len
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type","Accept","x-no-retry"));
//        gui kem cookie hay khong
        configuration.setAllowCredentials(true);
        //thoi gian pre-flight request co the cache
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);
        return source;

    }
}
