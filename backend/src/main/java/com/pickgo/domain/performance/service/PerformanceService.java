package com.pickgo.domain.performance.service;

import com.pickgo.domain.kopis.dto.KopisPerformanceDetailResponse;
import com.pickgo.domain.kopis.dto.KopisVenueDetailResponse;
import com.pickgo.domain.kopis.service.KopisService;
import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.repository.PerformanceRepository;
import com.pickgo.domain.performance.util.PerformanceMapper;
import com.pickgo.domain.post.post.service.PostService;
import com.pickgo.domain.venue.entity.Venue;
import com.pickgo.domain.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class PerformanceService {
    private final KopisService kopisService;
    private final PostService postService;
    private final PerformanceRepository performanceRepository;
    private final VenueRepository venueRepository;

    // 30개의 쓰레드 풀 생성
    private final ExecutorService executorService = Executors.newFixedThreadPool(30);

    // kopis api를 통해 받아온 공연 데이터를 저장
    public void fetchAndSavePerformances() {
        // 공연 id 리스트 조회
        List<String> performanceIds = kopisService.fetchPerformanceIds(1, 30);
        List<Future<?>> futures = new ArrayList<>();

        for (String performanceId : performanceIds) {
            futures.add(executorService.submit(() -> {
                try {
                    fetchAndSaveSinglePerformance(performanceId);
                } catch (Exception e) {
                    System.out.printf("%s 공연 저장 중 오류 발생: %s\n", performanceId, e.getMessage());
                }
            }));
        }

        // 모든 작업 대기
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fetchAndSaveSinglePerformance(String performanceId) {
        // 공연 상세 조회
        KopisPerformanceDetailResponse performanceDetailResponse = kopisService.fetchPerformanceDetail(performanceId);

        // 공연 중복 체크
        if (performanceRepository.existsByNameAndPoster(performanceDetailResponse.getName(), performanceDetailResponse.getPoster())) {
            return;
        }

        Venue venue = null;
        KopisVenueDetailResponse venueDetail = null;
        try {
            // 공연 시설 상세 조회 및 저장
            venueDetail = kopisService.fetchVenueDetail(performanceDetailResponse.getVenueId());
            venue = venueRepository.save(
                    Venue.builder()
                            .name(venueDetail.name())
                            .address(venueDetail.address())
                            .build());
        } catch (DataIntegrityViolationException e) {
            venue = venueRepository.findByNameAndAddress(venueDetail.name(), venueDetail.address()).orElseThrow();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // 공연 저장
        Performance performance = PerformanceMapper.toPerformance(performanceDetailResponse, venue);
        Performance saved = performanceRepository.save(performance);

        // 임시 게시글 생성
        postService.createPost(saved);
    }
}
