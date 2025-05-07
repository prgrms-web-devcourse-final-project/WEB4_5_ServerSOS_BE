package com.pickgo.area.seat;

import com.pickgo.domain.area.seat.dto.SeatResponse;
import com.pickgo.domain.area.seat.entity.SeatStatus;
import com.pickgo.domain.area.seat.service.SeatService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatService seatService;

    @Test
    @DisplayName("GET /api/areas/{areaId}/seats - 좌석 목록 조회 성공")
    void getSeats_success() throws Exception {
        // given
        SeatResponse seat1 = new SeatResponse(1L, 1L, 1L, "A", 1, SeatStatus.AVAILABLE, null);
        SeatResponse seat2 = new SeatResponse(2L, 1L, 1L, "A", 2, SeatStatus.RESERVED, null);

        when(seatService.getSeats(anyLong(), anyLong()))
                .thenReturn(List.of(seat1, seat2));

        // when & then
        mockMvc.perform(get("/api/areas/1/seats")
                        .param("sessionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("좌석 목록을 조회하였습니다."))
                .andExpect(jsonPath("$.data[0].seatId").value(1))
                .andExpect(jsonPath("$.data[0].row").value("A"))
                .andExpect(jsonPath("$.data[0].number").value("1"));
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
