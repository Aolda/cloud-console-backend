package com.acc.local.capture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaptureRateLimiter {

    @Value("${app.capture.rate.sleep-ms:50}")
    private long sleepMs;

    @Value("${app.capture.rate.jitter-ms:50}")
    private long jitterMs;

    @Getter
    @Value("${app.capture.rate.retries:2}")
    private int retries;

    @Getter
    @Value("${app.capture.rate.backoff-ms:200}")
    private long backoffMs;

    @Value("${app.capture.rate.cooldown-every:25}")
    private int cooldownEvery;

    @Value("${app.capture.rate.cooldown-ms:5000}")
    private long cooldownMs;

    private final AtomicInteger counter = new AtomicInteger();

    @Value("${app.capture.rate.startup-warmup-ms:0}")
    private long startupWarmupMs;

    public void pause() {
        long jitter = jitterMs > 0 ? ThreadLocalRandom.current().nextLong(jitterMs + 1) : 0;
        long delay = Math.max(0, sleepMs + jitter);
        if (delay <= 0) return;
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    public void backoffSleep(int attempt) {
        long delay = Math.max(0, backoffMs * Math.max(1, attempt));
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    public void maybeCooldown() {
        int n = counter.incrementAndGet();
        if (cooldownEvery > 0 && n % cooldownEvery == 0) {
            try {
                Thread.sleep(Math.max(0, cooldownMs));
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void warmup() {
        if (startupWarmupMs <= 0) return;
        try {
            Thread.sleep(startupWarmupMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
