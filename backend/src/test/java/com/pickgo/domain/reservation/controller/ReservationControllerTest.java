package com.pickgo.domain.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.log.entity.ReservationHistory;
import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.repository.ReservationHistoryRepository;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.performance.entity.PerformanceSession;
import com.pickgo.domain.reservation.dto.request.ReservationCreateRequest;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.global.init.TestDataInit;
import com.pickgo.global.logging.service.HistorySaveService;
import com.pickgo.token.TestToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataInit testDataInit;

    @Autowired
    private TestToken token;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HistorySaveService historySaveService;

    @Autowired
    private ReservationHistoryRepository reservationHistoryRepository;

    private Member member;
    private PerformanceSession session;
    private PerformanceArea area;

    @BeforeEach
    void setUp() {
        var data = testDataInit.create();
        this.member = data.member();
        this.session = data.session();
        this.area = data.area();
    }

    @BeforeEach
    void clearLog() {
        reservationHistoryRepository.deleteAll();
    }

    @Test
    @DisplayName("예약 성공 - 유저")
    void reserve_success() throws Exception {
        var seatDtos = List.of(
                new ReservationCreateRequest.SeatRequest(area.getId(), 1, 1),
                new ReservationCreateRequest.SeatRequest(area.getId(), 1, 2)
        );

        ReservationCreateRequest request = new ReservationCreateRequest(session.getId(), seatDtos);

        int expectedTotalPrice = area.getPrice() * seatDtos.size();

        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.userToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.memberId").value(member.getId().toString()))
                .andExpect(jsonPath("$.data.performance_session_id").value(session.getId()))
                .andExpect(jsonPath("$.data.status").value("RESERVED"))
                .andExpect(jsonPath("$.data.total_price").value(expectedTotalPrice))
                .andExpect(jsonPath("$.data.seats.length()").value(seatDtos.size()))
                .andExpect(jsonPath("$.data.seats[0].row").value(String.valueOf((char) ('A' + seatDtos.get(0).row() - 1))))
                .andExpect(jsonPath("$.data.seats[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data.seats[0].number").value(seatDtos.get(0).column()));


        // 💡 멤버의 연관관계 확인
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(foundMember.getReservations()).hasSize(1);

        Reservation savedReservation = foundMember.getReservations().get(0);
        assertThat(savedReservation.getReservedSeats()).hasSize(seatDtos.size());

        List<ReservationHistory> logs = reservationHistoryRepository.findAll();

        assertThat(logs).hasSize(1); // 로그가 하나 저장되었는지
        ReservationHistory log = logs.get(0);
        assertThat(log.getReservationId()).isEqualTo(savedReservation.getId());
        assertThat(log.getAction()).isEqualTo(ActionType.RESERVATION_CREATED);
        assertThat(log.getActorId()).isEqualTo(member.getId().toString());
    }

    @Test
    @DisplayName("예약 실패 - 존재하지 않는 공연 회차 ID")
    void reserve_fail_1() throws Exception {
        var seatDtos = List.of(
                new ReservationCreateRequest.SeatRequest(area.getId(), 1, 1),
                new ReservationCreateRequest.SeatRequest(area.getId(), 1, 2)
        );

        ReservationCreateRequest request = new ReservationCreateRequest(99999L, seatDtos);

        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.userToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 공연 회차입니다."));
    }

    @Test
    @DisplayName("예약 실패 - 일부 좌석이 존재하지 않음")
    void reserve_fail_2() throws Exception {
        var invalidSeatDtos = List.of(
                new ReservationCreateRequest.SeatRequest(area.getId(), 100, 100),
                new ReservationCreateRequest.SeatRequest(area.getId(), 100, 200)
        );

        ReservationCreateRequest request = new ReservationCreateRequest(session.getId(), invalidSeatDtos);

        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.userToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 좌석입니다."));
    }

    @Test
    @DisplayName("예약 상세 조회 성공")
    void getReservation_success() throws Exception {
        // given: 예약 생성
        var seatDtos = List.of(
                new ReservationCreateRequest.SeatRequest(area.getId(), 1, 1),
                new ReservationCreateRequest.SeatRequest(area.getId(), 1, 2)
        );

        ReservationCreateRequest request = new ReservationCreateRequest(session.getId(), seatDtos);

        String reservationId = mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.userToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // reservationId 파싱
        Long id = ((Integer) JsonPath.read(reservationId, "$.data.id")).longValue();

        // when & then: 상세 조회
        mvc.perform(get("/api/reservations/{id}", id)
                        .header("Authorization", "Bearer " + token.userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.memberId").value(member.getId().toString()))
                .andExpect(jsonPath("$.data.seats").isArray())
                .andExpect(jsonPath("$.data.performance.name").value(session.getPerformance().getName()));
    }

    @Test
    @DisplayName("예약 상세 조회 실패 - 존재하지 않는 예약")
    void getReservation_notFound() throws Exception {
        // given
        Long nonexistentId = 9999L;

        // when & then
        mvc.perform(get("/api/reservations/{id}", nonexistentId)
                        .header("Authorization", "Bearer " + token.userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("예약 상세 조회 실패 - 다른 유저의 예약")
    void getReservation_forbidden() throws Exception {
        // given: 예약 생성
        var seatDtos = List.of(
                new ReservationCreateRequest.SeatRequest(area.getId(), 1, 1),
                new ReservationCreateRequest.SeatRequest(area.getId(), 1, 2)
        );

        ReservationCreateRequest request = new ReservationCreateRequest(session.getId(), seatDtos);

        String responseBody = mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.userToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reservationId = ((Integer) JsonPath.read(responseBody, "$.data.id")).longValue();

        // when & then: 다른 사용자 토큰으로 요청
        mvc.perform(get("/api/reservations/{id}", reservationId)
                        .header("Authorization", "Bearer " + token.adminToken)) // 다른 유저
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("내 예약 목록 조회 성공")
    void getMyReservations_success() throws Exception {
        // given: 예약 1건 생성
        var seatDtos = List.of(
                new ReservationCreateRequest.SeatRequest(area.getId(), 1, 1),
                new ReservationCreateRequest.SeatRequest(area.getId(), 1, 2)
        );

        ReservationCreateRequest request = new ReservationCreateRequest(session.getId(), seatDtos);

        mvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + token.userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // when & then: 예약 목록 조회
        // 현재 예약 완료된 것이 없어서 목록에 조회안됨 -> 0개
        mvc.perform(get("/api/reservations/me")
                        .header("Authorization", "Bearer " + token.userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.items.length()").value(0));
    }

//    @Test
//    @DisplayName("예약 취소 성공")
//    void cancelReservation_success() throws Exception {
//        // given: 예약 생성
//        ReservationCreateRequest request = new ReservationCreateRequest(
//                session.getId(),
//                seats.stream().map(Seat::getId).toList()
//        );
//
//        String reservationResult = mvc.perform(post("/api/reservations")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", "Bearer " + token.userToken)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        Long reservationId = ((Integer) JsonPath.read(reservationResult, "$.data.id")).longValue();
//
//        // when & then: 예약 취소
//        mvc.perform(post("/api/reservations/{id}/cancel", reservationId)
//                        .header("Authorization", "Bearer " + token.userToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value("200"))
//                .andExpect(jsonPath("$.message").value("예매가 취소되었습니다."));
//    }

}