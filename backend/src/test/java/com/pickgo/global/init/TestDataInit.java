package com.pickgo.global.init;

import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.area.area.repository.PerformanceAreaRepository;
import com.pickgo.domain.area.seat.entity.Seat;
import com.pickgo.domain.area.seat.entity.SeatStatus;
import com.pickgo.domain.area.seat.repository.SeatRepository;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.entity.enums.Authority;
import com.pickgo.domain.member.entity.enums.SocialProvider;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.performance.entity.*;
import com.pickgo.domain.performance.repository.PerformanceRepository;
import com.pickgo.domain.performance.repository.PerformanceSessionRepository;
import com.pickgo.domain.performance.repository.VenueRepository;
import com.pickgo.global.jwt.JwtProvider;
import com.pickgo.token.TestToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class TestDataInit {

    private final MemberRepository memberRepository;
    private final VenueRepository venueRepository;
    private final PerformanceRepository performanceRepository;
    private final PerformanceSessionRepository sessionRepository;
    private final PerformanceAreaRepository areaRepository;
    private final SeatRepository seatRepository;

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
            SeatRepository seatRepository
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
        Venue venue = venueRepository.save(
                Venue.builder()
                        .name("테스트 공연장")
                        .address("서울시 테스트구")
                        .build()
        );

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
                        .productionCompany("테스트컴퍼니")
                        .type(PerformanceType.MUSICAL)
                        .venue(venue)
                        .build()
        );

        // 4. 공연 세션
        PerformanceSession session = sessionRepository.save(
                PerformanceSession.builder()
                        .performance(performance)
                        .performanceTime(LocalDateTime.now().plusDays(1))
                        .build()
        );

        // 5. 구역 및 좌석
        PerformanceArea area = areaRepository.save(
                PerformanceArea.builder()
                        .performance(performance)
                        .name("A구역")
                        .price(10000)
                        .build()
        );

        Seat seat1 = seatRepository.save(
                Seat.builder()
                        .row("A")
                        .number(1)
                        .status(SeatStatus.AVAILABLE)
                        .performanceArea(area)
                        .performanceSession(session)
                        .build()
        );

        Seat seat2 = seatRepository.save(
                Seat.builder()
                        .row("A")
                        .number(2)
                        .status(SeatStatus.AVAILABLE)
                        .performanceArea(area)
                        .performanceSession(session)
                        .build()
        );

        return new TestData(member, session, List.of(seat1, seat2));
    }

    public record TestData(Member member, PerformanceSession session, List<Seat> seats) {
    }
}
