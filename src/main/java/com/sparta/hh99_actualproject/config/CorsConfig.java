package com.sparta.hh99_actualproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedOrigins("https://dear-mylove.com")
                .allowedOrigins("https://www.dear-mylove.com")
                .allowedOrigins("https://develop.d3sp7wwij6l2vx.amplifyapp.com")
                .allowedHeaders("*")
                .allowedMethods("*");

        //.exposedHeaders(JwtFilter.AUTHORIZATION_HEADER); //JSON 으로 Token 내용 전달
    }


}