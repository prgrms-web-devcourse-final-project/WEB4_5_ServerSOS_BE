package com.pickgo.seat;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.area.seat.dto.SeatResponse;
import com.pickgo.domain.area.seat.dto.SeatUpdateRequest;
import com.pickgo.domain.area.seat.entity.SeatStatus;
import com.pickgo.domain.area.seat.service.SeatService;
import com.pickgo.global.response.RsCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatService seatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("좌석 목록 조회 - 성공")
    void getSeats_success() throws Exception {
        List<SeatResponse> mockSeats = List.of(
                new SeatResponse(1L, 1L, 1L, "A", 1, SeatStatus.AVAILABLE, LocalDateTime.of(2025, 4, 27, 10, 0)),
                new SeatResponse(2L, 1L, 1L, "A", 2, SeatStatus.RESERVED, LocalDateTime.of(2025, 4, 27, 10, 0))
        );

        Mockito.when(seatService.getSeats(1L, 1L)).thenReturn(mockSeats);

        mockMvc.perform(get("/api/areas/1/seats")
                        .param("sessionId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("좌석 목록을 조회하였습니다."))
                .andExpect(jsonPath("$.data[0].seatId").value(1))
                .andExpect(jsonPath("$.data[1].seatId").value(2));
    }
    @Test
    @DisplayName("좌석 상태 변경 - 성공")
    void updateSeatStatus_success() throws Exception {
        SeatUpdateRequest request = new SeatUpdateRequest(1L, SeatStatus.RESERVED);
        doNothing().when(seatService).updateSeatStatus(1L, SeatStatus.RESERVED);

        mockMvc.perform(post("/api/areas/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(RsCode.SUCCESS.getMessage()));
    }
}

