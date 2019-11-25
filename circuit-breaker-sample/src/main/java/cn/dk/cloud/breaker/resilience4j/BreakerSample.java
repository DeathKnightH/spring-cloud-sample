package cn.dk.cloud.breaker.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import java.time.Duration;

/**
 * 手动创建 Resilience4j 断路器
 * 直接使用 resilience4j-circuitbreaker 示例
 */
public class BreakerSample {
    private CircuitBreakerRegistry circuitBreakerRegistry;

    public BreakerSample() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)  //故障率阈值，默认为50% 设置为50%
                .waitDurationInOpenState(Duration.ofMillis(2000))   //断路器在从 open 状态转换到 half open 状态前保持 open 状态的时间 设置为2000毫秒
                .permittedNumberOfCallsInHalfOpenState(2)   //当断路器处于 half open 状态时，允许的调用数量，必须大于0，默认为10 设置为2
                .slidingWindowSize(2)   //当断路器处于关闭状态时，记录返回结果的滑动窗口的大小，默认为100 设置为2
                .build();
         circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    public CircuitBreakerRegistry getCircuitBreakerRegistry() {
        return circuitBreakerRegistry;
    }
}
