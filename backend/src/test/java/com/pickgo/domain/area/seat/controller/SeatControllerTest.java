package com.pickgo.domain.area.seat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.area.seat.dto.SeatUpdateRequest;
import com.pickgo.domain.area.seat.entity.SeatStatus;
import com.pickgo.domain.area.seat.service.SeatService;
import com.pickgo.global.response.RsCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SeatService seatService;

    @Test
    @DisplayName("좌석 목록 조회 성공")
    void getSeatsSuccess() throws Exception {
        // given
        when(seatService.getSeats(eq(1L), eq(1L)))
                .thenReturn(List.of());

        // when, then
        mockMvc.perform(get("/api/areas/1/seats")
                        .param("sessionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("좌석 목록을 조회하였습니다."));
    }

    @Test
    @DisplayName("좌석 상태 변경 요청 성공")
    void updateSeatStatusSuccess() throws Exception {
        // given
        SeatUpdateRequest request = new SeatUpdateRequest(1L, 1L, "A", 5, SeatStatus.RESERVED);

        // when, then
        mockMvc.perform(post("/api/areas/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()));
    }


    @Test
    @DisplayName("GET /api/areas/subscribe - SSE 연결 성공")
    void subscribe_success() throws Exception {
        // given
        SseEmitter emitter = new SseEmitter();
        when(seatService.subscribeToSeatUpdates(anyLong())).thenReturn(emitter);

        // when & then
        mockMvc.perform(get("/api/areas/subscribe")
                        .param("sessionId", "1")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk());
    }
}
