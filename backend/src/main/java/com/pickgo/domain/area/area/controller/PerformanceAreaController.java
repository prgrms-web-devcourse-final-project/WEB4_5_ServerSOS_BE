package com.pickgo.domain.area.area.controller;

import com.pickgo.domain.area.area.dto.PerformanceAreaDetailResponse;
import com.pickgo.domain.area.area.service.PerformanceAreaService;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.pickgo.global.response.RsCode.SUCCESS;

@RestController
@RequestMapping("api/areas")
@RequiredArgsConstructor
@Tag(name = "Area API", description = "Area API 엔드포인트")
public class PerformanceAreaController {
    private final PerformanceAreaService performanceAreaService;

    @GetMapping
    public RsData<List<PerformanceAreaDetailResponse>> getAreas(@RequestParam Long sessionId) {
        return RsData.from(SUCCESS, performanceAreaService.getAreas(sessionId));
    }
}
