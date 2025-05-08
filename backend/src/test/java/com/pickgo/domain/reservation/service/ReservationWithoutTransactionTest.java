package com.pickgo.domain.reservation.service;

import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.performance.entity.PerformanceSession;
import com.pickgo.domain.reservation.dto.request.ReservationCreateRequest;
import com.pickgo.domain.reservation.dto.response.ReservationSimpleResponse;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.domain.reservation.repository.ReservationRepository;
import com.pickgo.global.init.TestDataInit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ReservationWithoutTransactionTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TestDataInit testDataInit;

    private Member member;
    private PerformanceSession session;
    private PerformanceArea area;


    @BeforeEach
    void setUp() {
        // 테스트용 회원, 회차, 좌석 세팅
        var data = testDataInit.create(); // 테스트 데이터를 세팅하고 DTO 또는 record 반환한다고 가정
        this.member = data.member();
        this.session = data.session();
        this.area = data.area();
    }

    @Test
    @DisplayName("스케쥴러를 통한 예약 취소")
    void timeoutScheduler() throws InterruptedException {
        // given
        var seatDtos = List.of(
                new ReservationCreateRequest.SeatDto(area.getId(), 1, 1),
                new ReservationCreateRequest.SeatDto(area.getId(), 1, 2)
        );

        ReservationCreateRequest request = new ReservationCreateRequest(session.getId(), seatDtos);

        ReservationSimpleResponse response = reservationService.createReservation(member.getId(), request);

        // 생성 직후 상태는 RESERVED
        assertThat(ReservationStatus.RESERVED).isEqualTo(response.status());

        // 3초 대기
        Thread.sleep(3_000);

        // DB 다시 조회
        Reservation updated = reservationRepository.findById(response.id()).orElseThrow();

        // 상태가 CANCELED로 변경되었는지 확인
        assertThat(ReservationStatus.EXPIRED).isEqualTo(updated.getStatus());
    }
}