package cn.dk.cloud.breaker.resilience4j;

import java.util.Calendar;

/**
 * 用于测试断路器的模拟后端
 */
public class BackendSample {
    public String doSomething() {
        System.out.println("Backend execute success.");
        return "result sample";
    }

    public String doSomethingWrong() {
        System.out.println("An error occurs.");
        return "error";
    }

    public String checkTime() {
        Calendar instance = Calendar.getInstance();
        System.out.println(instance);
        return instance.toString();
    }
}
