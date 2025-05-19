package com.pickgo.global.config.thread;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 커넥션 풀 및 Executor 설정
 */
@Configuration
public class ExecutorConfig {

    /**
     * 사용 대상: @Async, 비동기 작업
     */
    @Bean
    @Primary
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100); // CorePoolSize: 항상 유지되는 스레드 수
        executor.setMaxPoolSize(100); // MaxPoolSize: 작업 큐가 다 찼을 때 확장 가능한 최대 스레드 수
        executor.setQueueCapacity(0); // QueueCapacity: CorePool이 꽉 찬 경우, 추가되는 작업은 작업 큐에 집어넣음. 이때 작업 큐 길이
        executor.setThreadNamePrefix("async-task-"); // ThreadNamePrefix: 스레드 식별 가능하도록 prefix 설정
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()); // MaxPool도 다 찼을 때는 호출한 스레드가 직접 수행
        executor.initialize();
        return executor;
    }

    /**
     * 사용 대상: @Scheduled
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10); // 스레드 풀 크기
        scheduler.setAwaitTerminationSeconds(60); // 스프링이나 JVM 종료 시, 스레드 풀에 남은 작업을 마무리할 때까지 기다리는 시간 (graceful shutdown)
        scheduler.setThreadNamePrefix("scheduler-task-"); // ThreadNamePrefix: 스레드 식별 가능하도록 prefix 설정
        scheduler.initialize();
        return scheduler;
    }
}
