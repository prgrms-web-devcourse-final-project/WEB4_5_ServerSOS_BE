package com.pickgo.domain.reservation.service;

import com.pickgo.domain.area.seat.entity.Seat;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.performance.entity.PerformanceSession;
import com.pickgo.domain.reservation.dto.request.ReservationCreateRequest;
import com.pickgo.domain.reservation.dto.response.ReservationSimpleResponse;
import com.pickgo.global.init.TestDataInit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TestDataInit testDataInit;

    private Member member;
    private PerformanceSession session;
    private List<Seat> seats;

    @BeforeEach
    void setUp() {
        // 테스트용 회원, 회차, 좌석 세팅
        var data = testDataInit.create(); // 테스트 데이터를 세팅하고 DTO 또는 record 반환한다고 가정
        this.member = data.member();
        this.session = data.session();
        this.seats = data.seats();
    }

    @Test
    void test_init() {
        // when
        TestDataInit.TestData result = testDataInit.create();

        // then
        assertThat(result).isNotNull();
        assertThat(result.member()).isNotNull();
        assertThat(result.session()).isNotNull();
        assertThat(result.seats()).isNotEmpty();
    }

    @Test
    void createReserve() {
        // given
        List<Long> seatIds = seats.stream()
                .map(Seat::getId)
                .collect(Collectors.toList());

        ReservationCreateRequest request = new ReservationCreateRequest(
                session.getId(),
                seatIds
        );

        // when
        ReservationSimpleResponse response = reservationService.createReservation(member.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.memberId()).isEqualTo(member.getId());
        assertThat(response.performance_session_id()).isEqualTo(session.getId());
        assertThat(response.seats()).hasSize(seatIds.size());
        assertThat(response.status().name()).isEqualTo("RESERVED");
    }

}