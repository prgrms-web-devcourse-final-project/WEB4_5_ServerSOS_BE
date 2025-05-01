package com.pickgo.domain.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.area.seat.entity.Seat;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.performance.entity.PerformanceSession;
import com.pickgo.domain.reservation.dto.request.ReservationCreateRequest;
import com.pickgo.domain.reservation.entity.PendingSeat;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.global.init.TestDataInit;
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

    private Member member;
    private PerformanceSession session;
    private List<Seat> seats;

    @BeforeEach
    void setUp() {
        var data = testDataInit.create();
        this.member = data.member();
        this.session = data.session();
        this.seats = data.seats();
    }

    @Test
    @DisplayName("예약 성공 - 유저")
    void reserve_success() throws Exception {
        ReservationCreateRequest request = new ReservationCreateRequest(
                session.getId(),
                seats.stream().map(Seat::getId).toList()
        );

        int expectedTotalPrice = seats.stream()
                .mapToInt(seat -> seat.getPerformanceArea().getPrice())
                .sum();

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
                .andExpect(jsonPath("$.data.seats.length()").value(seats.size()))
                .andExpect(jsonPath("$.data.seats[0].row").value(seats.get(0).getRow()))
                .andExpect(jsonPath("$.data.seats[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data.seats[0].number").value(seats.get(0).getNumber()));


        // 💡 멤버의 연관관계 확인
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(foundMember.getReservations()).hasSize(1);

        Reservation savedReservation = foundMember.getReservations().get(0);
        assertThat(savedReservation.getPendingSeats()).hasSize(seats.size());

        // 💡 PendingSeat에 좌석 연관관계 확인
        for (int i = 0; i < seats.size(); i++) {
            Seat expectedSeat = seats.get(i);
            PendingSeat pendingSeat = savedReservation.getPendingSeats().get(i);

            assertThat(pendingSeat.getSeat().getId()).isEqualTo(expectedSeat.getId());
            assertThat(pendingSeat.getReservation().getId()).isEqualTo(savedReservation.getId());
        }
    }

    @Test
    @DisplayName("예약 실패 - 존재하지 않는 공연 회차 ID")
    void reserve_fail_1() throws Exception {
        ReservationCreateRequest request = new ReservationCreateRequest(
                99999L,  // 존재하지 않는 회차 ID
                seats.stream().map(Seat::getId).toList()
        );

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
        List<Long> invalidSeatIds = List.of(999L, 1000L);  // 존재하지 않는 좌석

        ReservationCreateRequest request = new ReservationCreateRequest(
                session.getId(),
                invalidSeatIds
        );

        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.userToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("요청하신 리소스를 찾을 수 없습니다."));
    }

}