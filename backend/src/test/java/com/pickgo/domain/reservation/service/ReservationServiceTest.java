package com.pickgo.domain.reservation.service;

import com.pickgo.domain.area.seat.entity.Seat;
import com.pickgo.domain.area.seat.repository.SeatRepository;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.performance.entity.PerformanceSession;
import com.pickgo.domain.reservation.dto.request.ReservationCreateRequest;
import com.pickgo.domain.reservation.dto.response.ReservationDetailResponse;
import com.pickgo.domain.reservation.dto.response.ReservationSimpleResponse;
import com.pickgo.domain.reservation.repository.ReservationRepository;
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
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private SeatRepository seatRepository;

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

    @Test
    void getReservation() {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(
                session.getId(),
                seats.stream().map(Seat::getId).toList()
        );
        ReservationSimpleResponse simpleResponse = reservationService.createReservation(member.getId(), request);
        Long reservationId = simpleResponse.id();

        // when
        ReservationDetailResponse response = reservationService.getReservation(reservationId, member.getId());


        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(reservationId);
        assertThat(response.memberId()).isEqualTo(member.getId());
        assertThat(response.performance().name()).isEqualTo(session.getPerformance().getName());
        assertThat(response.venue().name()).isEqualTo(session.getPerformance().getVenue().getName());
        assertThat(response.seats()).hasSize(seats.size());
    }


//    @Test
//    @DisplayName("예약 취소 성공")
//    void cancelReservation_success() {
//        // given: 예약 생성
//        ReservationCreateRequest request = new ReservationCreateRequest(
//                session.getId(),
//                seats.stream().map(Seat::getId).toList()
//        );
//
//        ReservationSimpleResponse response = reservationService.createReservation(member.getId(), request);
//        Long reservationId = response.id();
//
//        // when
//        reservationService.cancelReservation(reservationId);
//
//        // then: 상태가 CANCELED 인지 확인
//        Reservation canceled = reservationRepository.findById(reservationId)
//                .orElseThrow();
//
//        assertThat(canceled.getStatus()).isEqualTo(ReservationStatus.CANCELED);
//        assertThat(canceled.getPendingSeats()).isEmpty();
//
//        // 좌석 상태도 AVAILABLE로 복구됐는지 확인
//        for (Seat seat : seats) {
//            Seat updatedSeat = seatRepository.findById(seat.getId()).orElseThrow();
//            assertThat(updatedSeat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
//        }
//    }
}