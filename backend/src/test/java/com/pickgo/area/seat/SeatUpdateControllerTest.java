package com.pickgo.area.seat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.area.seat.dto.SeatUpdateRequest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SeatUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatService seatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/seats/update-status - 좌석 상태 변경 성공")
    void updateSeatStatus_success() throws Exception {
        // given
        SeatUpdateRequest request = new SeatUpdateRequest(123L, SeatStatus.RESERVED);

        // seatService 호출 시 아무 동작도 하지 않도록 설정
        doNothing().when(seatService).updateSeatStatus(anyLong(), any());

        // when & then
        mockMvc.perform(post("/api/seats/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").exists());
    }
}