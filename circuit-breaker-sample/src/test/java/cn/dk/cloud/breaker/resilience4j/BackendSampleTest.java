package cn.dk.cloud.breaker.resilience4j;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class BackendSampleTest {

    @Test
    public void doSomething() {
        CircuitBreaker circuitBreaker = new BreakerSample().getCircuitBreakerRegistry().circuitBreaker("test");
        BackendSample backendSample = new BackendSample();
        CheckedFunction0<String> checkedSupplier = circuitBreaker.decorateCheckedSupplier(backendSample::doSomething);
        Try<String> result = Try.of(checkedSupplier);
        System.out.println(result.isSuccess());
        System.out.println(result.get());
    }

    @Test
    public void openBreaker() {
        CircuitBreaker circuitBreaker = new BreakerSample().getCircuitBreakerRegistry().circuitBreaker("test");
        BackendSample backendSample = new BackendSample();
        CheckedRunnable checkedSupplier = circuitBreaker.decorateCheckedRunnable(backendSample::doSomething);
        CheckedFunction0<String> checkedSupplier1 = circuitBreaker.decorateCheckedSupplier(backendSample::doSomethingWrong);
        circuitBreaker.onError(1, TimeUnit.SECONDS, new RuntimeException());
        System.out.println(circuitBreaker.getState());
        CircuitBreaker.Metrics metrics1 = circuitBreaker.getMetrics();
        System.out.println("FailureRate: " + metrics1.getFailureRate());
        System.out.println("FailedCalls: " + metrics1.getNumberOfFailedCalls());
        Try<String> result = Try.of(checkedSupplier1).filterTry(string -> {
            if ("error".equals(string)) {
                circuitBreaker.onError(1, TimeUnit.SECONDS, new RuntimeException());    //模拟异常
                return false;
            }
            System.out.println(circuitBreaker.getState());
            return true;
        }).onFailure(throwable -> {
            CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
            System.out.println("FailureRate: " + metrics.getFailureRate());
            System.out.println("FailedCalls: " + metrics.getNumberOfFailedCalls());
            circuitBreaker.reset();
        }).andThenTry(checkedSupplier);

        System.out.println(circuitBreaker.getState());
        CircuitBreaker.Metrics metrics2 = circuitBreaker.getMetrics();
        System.out.println("FailureRate: " + metrics2.getFailureRate());
        System.out.println("FailedCalls: " + metrics2.getNumberOfFailedCalls());
        System.out.println(result);
    }

    @Test
    public void recover() {
        CircuitBreaker circuitBreaker = new BreakerSample().getCircuitBreakerRegistry().circuitBreaker("recover_test");
        BackendSample backendSample = new BackendSample();
        CheckedFunction0<Object> error_occurs = circuitBreaker.decorateCheckedSupplier(() -> {
            throw new RuntimeException("Error occurs"); //模拟异常
        });
        Try<Object> recover = Try.of(error_occurs)
                .recover(throwable -> "Please wait~");  //当断路器记录失败后，从异常中恢复
        System.out.println(recover.isSuccess());
        System.out.println(recover.get());
    }

    @Test
    public void rateLimit() {
        RateLimiter rateLimiter = new RateLimiterSample().getRateLimiterRegistry().rateLimiter("test limit");
        BackendSample backendSample = new BackendSample();
        CheckedRunnable checkedRunnable = RateLimiter.decorateCheckedRunnable(rateLimiter, backendSample::checkTime);
        //同时模拟5个请求，可见每秒只通过最多两个请求
        Try.run(checkedRunnable)
                .andThenTry(checkedRunnable)
                .andThenTry(checkedRunnable)
                .andThenTry(checkedRunnable)
                .andThenTry(checkedRunnable)
                .onFailure(throwable -> System.out.println(throwable.getLocalizedMessage()));
        //可以随时修改限流器参数，修改后会在下一个限流周期生效，本周期内不变
        rateLimiter.changeLimitForPeriod(100);
        rateLimiter.changeTimeoutDuration(Duration.ofSeconds(2));
    }
}