package com.pickgo.domain.performance.kopis.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pickgo.domain.performance.kopis.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
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
    @Retryable(
            retryFor = {IOException.class, HttpClientErrorException.class, RuntimeException.class},
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
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
    @Retryable(
            retryFor = {IOException.class, HttpClientErrorException.class, RuntimeException.class},
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
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
    @Retryable(
            retryFor = {IOException.class, HttpClientErrorException.class, RuntimeException.class},
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
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

    @Recover
    public List<String> recoverPerformanceIds(RuntimeException e, int page, int size) {
        log.warn("공연 목록 요청 재시도 실패 (page={}, size={}): {}", page, size, e.getMessage());
        return List.of();
    }

    @Recover
    public KopisPerformanceDetailResponse recoverPerformance(RuntimeException e, String id) {
        log.warn("공연 상세 요청 재시도 실패 (id={}): {}", id, e.getMessage());
        return null;
    }

    @Recover
    public KopisVenueDetailResponse recoverVenue(RuntimeException e, String id) {
        log.warn("공연장 상세 요청 재시도 실패 (id={}): {}", id, e.getMessage());
        return null;
    }
}
