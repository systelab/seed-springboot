package com.systelab.seed.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class WebServerConfig {

    private final HttpServerPortConfig httpServerPortConfig;

    @Bean
    public UndertowServletWebServerFactory embeddedUndertowServletWebServer() {

        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();

        int port = httpServerPortConfig.getPort();
        if (port > 0)
            factory.addBuilderCustomizers(builder -> builder.addHttpListener(port, "0.0.0.0"));
        return factory;
    }
}
