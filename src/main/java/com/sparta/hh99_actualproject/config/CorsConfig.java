package com.sparta.hh99_actualproject.config;

import com.sparta.hh99_actualproject.jwt.JwtFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://dear-d41a5.web.app/")
                .allowedOrigins("http://localhost:3000")
                .allowedOrigins("*")
                .allowedMethods("*");
//                .exposedHeaders(JwtFilter.AUTHORIZATION_HEADER); //JSON 으로 Token 내용 전달
    }
}