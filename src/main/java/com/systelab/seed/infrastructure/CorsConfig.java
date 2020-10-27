package com.systelab.seed.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@ConfigurationProperties(prefix = "security")
public class CorsConfig {

    private final CorsConfiguration cors = new CorsConfiguration();

    public CorsConfiguration getCors() {
        return cors;
    }
}
