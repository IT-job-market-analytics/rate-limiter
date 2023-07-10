package com.example.ratelimiter.service;

import com.example.ratelimiter.exception.NotFoundException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BlockingStrategy;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucket;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBucketService {
    static final long MAX_WAIT_NANOS = TimeUnit.HOURS.toNanos(1);
    private final Map<String, Long> quotas;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public void consumeQuota(String operation_id) throws InterruptedException {
        checkId(operation_id);
        Bucket bucket = buckets.get(operation_id);

        log.debug(
                "Consuming quota for operation \"" + operation_id + "\""
                + ", available tokens: " + bucket.getAvailableTokens()
        );

        while (true) {
            if (bucket.asBlocking().tryConsume(1, MAX_WAIT_NANOS, BlockingStrategy.PARKING)) {
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
            String operationId = entry.getKey();
            Long rps = entry.getValue();

            // tokens regenerate greedily (not each second, at 1/RPS interval).
            // example - for RPS 10, tokens will refill each 100ms
            Refill refill = Refill.greedy(rps, Duration.ofSeconds(1L));
            Bandwidth limit = Bandwidth.classic(rps, refill);
            LocalBucket bucket = Bucket.builder().addLimit(limit).build();

            buckets.put(operationId, bucket);

            log.info("Create bucket for operation \"" + operationId + "\", RPS = " + rps);
        }
    }
}
