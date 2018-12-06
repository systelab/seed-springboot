package com.systelab.seed;

import com.systelab.seed.config.NonSecureHttpServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableFeignClients
@EnableCaching
@SpringBootApplication
public class App {

    @Autowired
    NonSecureHttpServerConfig properties;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public UndertowServletWebServerFactory embeddedUndertowServletWebServer() {

        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();

        int port = properties.getPort();
        if (port > 0)
            factory.addBuilderCustomizers(builder -> builder.addHttpListener(port, "0.0.0.0"));
        return factory;
    }
}

