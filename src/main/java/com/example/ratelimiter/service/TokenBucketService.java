package com.example.ratelimiter.service;

import com.example.ratelimiter.exception.BadRequestException;
import com.example.ratelimiter.exception.NotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.isomorphism.util.TokenBucket;
import org.isomorphism.util.TokenBuckets;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBucketService {
    private final Map<String, Double> quotas;
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public void consumeQuota(String operation_id) {
        checkId(operation_id);
        buckets.get(operation_id).consume();
    }

    private void checkId(String operation_id) {
        if (!buckets.containsKey(operation_id)) {
            throw new NotFoundException("Unsupported operation_id");
        }
    }

    @PostConstruct
    private void fillBucketsMap() {
        for (Map.Entry<String, Double> entry : quotas.entrySet()) {
            if (entry.getValue() >= 1.0) {
                buckets.put(entry.getKey(), TokenBuckets.builder()
                        .withCapacity(entry.getValue().longValue())
                        .withFixedIntervalRefillStrategy(entry.getValue().longValue(), 1L, TimeUnit.SECONDS)
                        .build());
            } else {
                Double period = Math.ceil(1 / entry.getValue());
                buckets.put(entry.getKey(), TokenBuckets.builder()
                        .withCapacity(1L)
                        .withFixedIntervalRefillStrategy(1L, period.longValue(), TimeUnit.SECONDS)
                        .build());
            }
        }
    }
}
