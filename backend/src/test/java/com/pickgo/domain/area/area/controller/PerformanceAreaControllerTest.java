package com.pickgo.domain.area.area.controller;

import com.pickgo.domain.area.area.dto.PerformanceAreaDetailResponse;
import com.pickgo.domain.area.area.service.PerformanceAreaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class PerformanceAreaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PerformanceAreaService performanceAreaService;

    @Test
    @DisplayName("구역 조회 테스트")
    void getAreas() throws Exception {
        Long sessionId = 1L;

        List<PerformanceAreaDetailResponse> dummyResponse = List.of(
                new PerformanceAreaDetailResponse(1L, "VIP 구역", "P석", 100000, 5, 10, List.of()),
                new PerformanceAreaDetailResponse(2L, "A 구역", "R석", 80000, 6, 10, List.of())
        );

        given(performanceAreaService.getAreas(sessionId)).willReturn(dummyResponse);

        mockMvc.perform(get("/api/areas")
                        .param("sessionId", sessionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("VIP 구역"))
                .andExpect(jsonPath("$.data[0].grade").value("P석"))
                .andExpect(jsonPath("$.data[1].name").value("A 구역"))
                .andExpect(jsonPath("$.data[1].grade").value("R석"));
    }
}
