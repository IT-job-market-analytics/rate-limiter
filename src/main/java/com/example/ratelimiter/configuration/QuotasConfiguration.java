package com.example.ratelimiter.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("ConfigurationProperties")
@Configuration
@ConfigurationProperties
@Getter
public class QuotasConfiguration {

    private final Map<String, Long> rpsQuotas = new HashMap<>();
}
