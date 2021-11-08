package com.systelab.seed.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "http-server")
@Getter
@Setter
public class HttpServerPortConfig {

    private int port;
}