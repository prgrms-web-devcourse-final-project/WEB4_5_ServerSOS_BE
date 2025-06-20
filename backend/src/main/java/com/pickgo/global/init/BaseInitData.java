package com.pickgo.global.init;

import com.pickgo.domain.member.member.dto.MemberCreateRequest;
import com.pickgo.domain.member.member.service.MemberService;
import com.pickgo.domain.performance.performance.repository.PerformanceRepository;
import com.pickgo.domain.performance.performance.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@RequiredArgsConstructor
@Profile("!test") // 테스트 환경에서는 동작 X
public class BaseInitData {
    private final PerformanceService performanceService;
    private final PerformanceRepository performanceRepository;
    private final MemberService memberService;
    private final StringRedisTemplate redisTemplate;


    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            // 필요 시 주석 해제 후 사용 데이터 저장에 10분 가량 소요될 수 있음
            if (performanceRepository.count() <= 0) {
                performanceService.fetchAndSavePerformances();
                System.out.println("초기 데이터 세팅이 완료되었습니다.");
            }

            String testEmail = "test@example.com";
            String password = "1234";
            String nickname = "test1234";
            if (!memberService.existsByEmail(testEmail)) {
                // 이메일 인증 통과하도록 설정
                redisTemplate.opsForValue().set("email:verify:success:" + testEmail, "true");
                memberService.save(new MemberCreateRequest(testEmail, password, nickname));
                System.out.println("회원가입이 완료되었습니다");
            }

            String adminEmail = "admin@example.com";
            String adminNickname = "admin1234";
            if (!memberService.existsByEmail(adminEmail)) {
                memberService.saveAdmin(new MemberCreateRequest(adminEmail, password, adminNickname));
                System.out.println("관리자 회원가입이 완료되었습니다");
            }
        };
    }
}
