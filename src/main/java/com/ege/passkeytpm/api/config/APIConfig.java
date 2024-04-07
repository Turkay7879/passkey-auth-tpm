package com.ege.passkeytpm.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class APIConfig implements WebMvcConfigurer {
    
    @SuppressWarnings({ "deprecation" })
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security.csrf(csrf -> csrf.disable()).authorizeRequests(authorize -> authorize.anyRequest().permitAll());
        return security.build();    
    }

    @Override
    public void addCorsMappings(@SuppressWarnings("null") CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOrigins("http://localhost:8080", "http://localhost:5173")
        .allowedMethods("GET", "POST", "DELETE")
        .allowedHeaders("*")
        .allowCredentials(true);
    }

    @Override
    public void configurePathMatch(@SuppressWarnings("null") PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api", (clazz) -> true);
    }
}
