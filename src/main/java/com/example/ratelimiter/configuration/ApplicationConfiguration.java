package com.example.ratelimiter.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {
    private final QuotasConfiguration quotasConfiguration;

    @Bean
    public Map<String, Long> getRpsQuotas() {
        return Collections.unmodifiableMap(quotasConfiguration.getRpsQuotas());
    }
}
