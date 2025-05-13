package com.pickgo.global.init;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pickgo.domain.performance.area.area.entity.AreaGrade;
import com.pickgo.domain.performance.area.area.entity.AreaName;
import com.pickgo.domain.performance.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.area.area.repository.PerformanceAreaRepository;
import com.pickgo.domain.performance.area.seat.repository.ReservedSeatRepository;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.member.member.entity.enums.Authority;
import com.pickgo.domain.member.member.entity.enums.SocialProvider;
import com.pickgo.domain.member.member.repository.MemberRepository;
import com.pickgo.domain.performance.performance.entity.Performance;
import com.pickgo.domain.performance.performance.entity.PerformanceSession;
import com.pickgo.domain.performance.performance.entity.PerformanceState;
import com.pickgo.domain.performance.performance.entity.PerformanceType;
import com.pickgo.domain.performance.performance.repository.PerformanceRepository;
import com.pickgo.domain.performance.performance.repository.PerformanceSessionRepository;
import com.pickgo.domain.performance.venue.entity.Venue;
import com.pickgo.domain.performance.venue.repository.VenueRepository;
import com.pickgo.global.jwt.JwtProvider;
import com.pickgo.global.token.TestToken;

@Component
@Profile("test")
public class TestDataInit {

    private final MemberRepository memberRepository;
    private final VenueRepository venueRepository;
    private final PerformanceRepository performanceRepository;
    private final PerformanceSessionRepository sessionRepository;
    private final PerformanceAreaRepository areaRepository;
    private final ReservedSeatRepository seatRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private TestToken token;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public TestDataInit(
            MemberRepository memberRepository,
            VenueRepository venueRepository,
            PerformanceRepository performanceRepository,
            PerformanceSessionRepository sessionRepository,
            PerformanceAreaRepository areaRepository,
            ReservedSeatRepository seatRepository
    ) {
        this.memberRepository = memberRepository;
        this.venueRepository = venueRepository;
        this.performanceRepository = performanceRepository;
        this.sessionRepository = sessionRepository;
        this.areaRepository = areaRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public TestData create() {
        // 1. 토큰에서 userId 추출
        UUID userId = jwtProvider.getUserId(token.userToken);

        // 2. Member 저장
        Member member = memberRepository.save(Member.builder()
                .id(userId)
                .email("test@example.com")
                .password(passwordEncoder.encode("test_password"))
                .nickname("test_user")
                .authority(Authority.USER)
                .socialProvider(SocialProvider.NONE)
                .build());

        // 2. 공연장
        Venue venue = venueRepository.findByNameAndAddress("테스트 공연장", "서울시 테스트구")
                .orElseGet(() -> venueRepository.save(
                        Venue.builder()
                                .name("테스트 공연장")
                                .address("서울시 테스트구")
                                .build()
                ));

        // 3. 공연
        Performance performance = performanceRepository.save(
                Performance.builder()
                        .name("테스트 공연")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(1))
                        .runtime("120분")
                        .poster("test.jpg")
                        .state(PerformanceState.SCHEDULED)
                        .minAge("전체관람가")
                        .casts("홍길동 외")
                        .type(PerformanceType.MUSICAL)
                        .venue(venue)
                        .build()
        );

        // 4. 공연 세션
        PerformanceSession session = sessionRepository.save(
                PerformanceSession.builder()
                        .performance(performance)
                        .performanceTime(LocalDateTime.now().plusDays(1))
                        .reserveOpenAt(LocalDateTime.now().minusDays(10))
                        .build()
        );

        // 5. 구역
        PerformanceArea area = areaRepository.save(
                PerformanceArea.builder()
                        .performance(performance)
                        .name(AreaName.A)
                        .grade(AreaGrade.ROYAL)
                        .price(10000)
                        .rowCount(15)
                        .colCount(10)
                        .build()
        );

        return new TestData(member, session, area);
    }

    public record TestData(Member member, PerformanceSession session, PerformanceArea area) {
    }
}
