package com.pickgo.domain.performance.scheduler;

import com.pickgo.domain.performance.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("prod") // 운영 환경에서만 작동
public class PerformanceScheduler {
    private final PerformanceService performanceService;

    // 매일 정오에 업데이트
    @Scheduled(cron = "0 0 12 * * *")
    public void updatePerformanceDaily() {
        try {
            performanceService.fetchAndSavePerformances();
        } catch (Exception e) {
            System.out.println("업데이트 중 에러 발생: " + e.getMessage());
        }
    }
}
