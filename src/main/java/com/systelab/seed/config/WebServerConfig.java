package com.systelab.seed.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServerConfig {

    private final NonSecureHttpServerConfig nonSecureHttpServerConfig;

    @Autowired
    public WebServerConfig(NonSecureHttpServerConfig nonSecureHttpServerConfig) {
        this.nonSecureHttpServerConfig = nonSecureHttpServerConfig;
    }

    @Bean
    public UndertowServletWebServerFactory embeddedUndertowServletWebServer() {

        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();

        int port = nonSecureHttpServerConfig.getPort();
        if (port > 0)
            factory.addBuilderCustomizers(builder -> builder.addHttpListener(port, "0.0.0.0"));
        return factory;
    }
}
