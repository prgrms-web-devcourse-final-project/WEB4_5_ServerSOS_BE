package com.pickgo.global.init;

import com.pickgo.domain.performance.repository.PerformanceRepository;
import com.pickgo.domain.performance.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("!test") // 테스트 환경에서는 동작 X
public class BaseInitData {
    private final PerformanceService performanceService;
    private final PerformanceRepository performanceRepository;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            if (performanceRepository.count() > 0) {
                return;
            }

            // 필요 시 주석 해제 후 사용 데이터 저장에 10분 가량 소요될 수 있음
            performanceService.fetchAndSavePerformances();
            System.out.println("초기 데이터 세팅이 완료되었습니다.");
        };
    }
}
