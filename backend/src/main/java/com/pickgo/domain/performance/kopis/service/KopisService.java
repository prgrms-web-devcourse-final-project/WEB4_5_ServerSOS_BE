package com.pickgo.domain.performance.kopis.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pickgo.domain.kopis.dto.*;
import com.pickgo.domain.performance.kopis.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class KopisService {
    private final RestClient restClient;
    private final XmlMapper xmlMapper;
    @Value("${kopis.apikey}")
    private String apikey;

    public KopisService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://kopis.or.kr/openApi/restful").build();
        this.xmlMapper = new XmlMapper();
    }

    // 공연 목록
    public List<String> fetchPerformanceIds(int page, int size) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(3);

        String xml = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pblprfr")
                        .queryParam("service", apikey)
                        .queryParam("stdate", startDate.format(DateTimeFormatter.BASIC_ISO_DATE))
                        .queryParam("eddate", endDate.format(DateTimeFormatter.BASIC_ISO_DATE))
                        .queryParam("cpage", page)
                        .queryParam("rows", size)
                        .build())
                .retrieve()
                .body(String.class);

        try {
            KopisPerformanceListResponse response = xmlMapper.readValue(xml, KopisPerformanceListResponse.class);
            return response.performances().stream()
                    .map(KopisPerformanceListResponse.KopisPerformanceDto::id).toList();
        } catch (Exception e) {
            throw new RuntimeException("공연 목록 xml 파싱 실패", e);
        }
    }

    // 공연 상세
    public KopisPerformanceDetailResponse fetchPerformanceDetail(String performanceId) {
        String xml = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pblprfr/" + performanceId)
                        .queryParam("service", apikey)
                        .build())
                .retrieve()
                .body(String.class);
        try {
            return xmlMapper.readValue(xml, KopisPerformanceDetailWrapper.class).detail();
        } catch (Exception e) {
            throw new RuntimeException("공연 상세 xml 파싱 실패", e);
        }
    }

    // 공연 시설 상세
    public KopisVenueDetailResponse fetchVenueDetail(String venueId) {
        String xml = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/prfplc/" + venueId.trim())
                        .queryParam("service", apikey)
                        .build())
                .retrieve()
                .body(String.class);
        try {
            return xmlMapper.readValue(xml, KopisVenueDetailWrapper.class).detail();
        } catch (Exception e) {
            throw new RuntimeException("공연 시설 상세 xml 파싱 실패", e);
        }
    }
}
