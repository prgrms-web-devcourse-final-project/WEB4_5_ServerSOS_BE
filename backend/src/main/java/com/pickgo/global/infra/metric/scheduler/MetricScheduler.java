package com.pickgo.global.infra.metric.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pickgo.global.infra.metric.repository.redis.RedisMetricRepository;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MetricScheduler {

    private long lastCount = 0;
    private final MeterRegistry meterRegistry;
    private final RedisMetricRepository redisMetricsRepository;

    /**
     * 누적 카운터 기반 TPS 계산
     */
    @Scheduled(fixedRate = 1000)
    public void collectTps() {
        Timer timer = meterRegistry
                .find("http.server.requests")
                .timer();

        if (timer == null)
            return;

        long count = timer.count(); // 현재까지 누적 요청 수
        long tps = count - lastCount; // 지난 1초간 처리된 요청 수
        lastCount = count;

        redisMetricsRepository.saveTps(tps);
    }
}
