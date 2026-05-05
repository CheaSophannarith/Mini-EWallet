package com.fii.ewallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PathConfig {

    @Bean(name = "publicPaths")
    public List<String> publicPaths() {
        return List.of("/api/v1/auth/**");
    }

}
