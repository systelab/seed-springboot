package com.systelab.seed.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "http-server")
public class NonSecureHttpServerConfigurationProperties {

    private int port;
    // standard getters and setters

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


}