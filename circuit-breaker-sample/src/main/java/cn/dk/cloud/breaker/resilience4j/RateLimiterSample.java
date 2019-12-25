package cn.dk.cloud.breaker.resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

import java.time.Duration;

/**
 * 手动创建限流器 RateLimiter
 */
public class RateLimiterSample {
    private RateLimiterRegistry rateLimiterRegistry;

    public RateLimiterSample() {
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(1)) //等待超时时间，默认5秒 设置为3秒
                .limitForPeriod(2)  //一个限流周期内运行通过的 calls 数量，默认50个 设置为2
                .limitRefreshPeriod(Duration.ofSeconds(2))  //限流周期，其实也是限制刷新周期，默认500纳秒 设置为1秒
                .build();
        this.rateLimiterRegistry = RateLimiterRegistry.of(rateLimiterConfig);
    }

    public RateLimiterRegistry getRateLimiterRegistry() {
        return rateLimiterRegistry;
    }
}
