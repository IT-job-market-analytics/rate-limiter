package com.example.ratelimiter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class QuotasConfiguration {

    @Bean
    @Scope("prototype")
    @ConfigurationProperties("rps-quotas")
    protected Map<String, Double> getRpsQuotas() {
        return new HashMap<>();
    }
}
