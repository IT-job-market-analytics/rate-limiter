package com.example.ratelimiter.service;

import com.example.ratelimiter.exception.NotFoundException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BlockingStrategy;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBucketService {
    static final long MAX_WAIT_NANOS = TimeUnit.HOURS.toNanos(1);
    private final Map<String, Long> quotas;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public void consumeQuota(String operation_id) throws InterruptedException {
        checkId(operation_id);
        System.out.println(buckets.get(operation_id).getAvailableTokens());


        while (true) {
            if (buckets.get(operation_id).asBlocking().tryConsume(1, MAX_WAIT_NANOS, BlockingStrategy.PARKING)) {
                break;
            }
        }
    }

    private void checkId(String operation_id) {
        if (!buckets.containsKey(operation_id)) {
            throw new NotFoundException("Unsupported operation_id");
        }
    }

    @PostConstruct
    private void fillBucketsMap() {
        for (Map.Entry<String, Long> entry : quotas.entrySet()) {
            Refill refill = Refill.greedy(entry.getValue(), Duration.ofSeconds(1L));
            Bandwidth limit = Bandwidth.classic(entry.getValue(), refill);
            buckets.put(entry.getKey(), Bucket.builder()
                    .addLimit(limit)
                    .build());
        }
    }
}
