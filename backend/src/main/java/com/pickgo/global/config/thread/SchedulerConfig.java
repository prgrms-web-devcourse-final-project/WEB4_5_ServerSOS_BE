package com.pickgo.global.config.thread;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import lombok.RequiredArgsConstructor;

/**
 * 스케줄러 관련 설정
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig implements SchedulingConfigurer {

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }
}
