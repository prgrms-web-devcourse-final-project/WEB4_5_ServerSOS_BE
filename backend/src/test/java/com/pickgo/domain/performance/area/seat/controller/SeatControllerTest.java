package com.pickgo.domain.performance.area.seat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.performance.area.seat.service.SeatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
