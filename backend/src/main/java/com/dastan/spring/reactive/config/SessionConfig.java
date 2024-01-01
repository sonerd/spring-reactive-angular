package com.dastan.spring.reactive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

@Configuration
public class SessionConfig {

    public final static String XAUTHTOKEN = "X-AUTH-TOKEN";

    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        var resolver = new HeaderWebSessionIdResolver();
        resolver.setHeaderName(XAUTHTOKEN);
        return resolver;
    }
}
