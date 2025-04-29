package com.pickgo.global.init;

import com.pickgo.domain.performance.repository.PerformanceRepository;
import com.pickgo.domain.performance.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    private final PerformanceService performanceService;
    private final PerformanceRepository performanceRepository;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            if (performanceRepository.count() > 0) {
                return;
            }

            performanceService.fetchAndSavePerformances();
            System.out.println("초기 데이터 세팅이 완료되었습니다.");
        };
    }
}
