package com.pickgo.domain.performance.service;

import com.pickgo.domain.kopis.dto.KopisPerformanceDetailResponse;
import com.pickgo.domain.kopis.dto.KopisVenueDetailResponse;
import com.pickgo.domain.kopis.service.KopisService;
import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.repository.PerformanceRepository;
import com.pickgo.domain.performance.util.PerformanceMapper;
import com.pickgo.domain.post.service.PostService;
import com.pickgo.domain.venue.entity.Venue;
import com.pickgo.domain.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceService {
    private final KopisService kopisService;
    private final PostService postService;
    private final PerformanceRepository performanceRepository;
    private final VenueRepository venueRepository;

    // kopis api를 통해 받아온 공연 데이터를 저장
    @Transactional
    public void fetchAndSavePerformances() {
        // 공연 id 리스트 조회
        List<String> performanceIds = kopisService.fetchPerformanceIds(1, 30);

        for (String performanceId : performanceIds) {
            // 공연 상세 정보 조회
            KopisPerformanceDetailResponse performanceDetail;
            try {
                performanceDetail = kopisService.fetchPerformanceDetail(performanceId);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }

            // 공연 시설 상세 조회
            Venue venue = null;
            try {
                KopisVenueDetailResponse venueDetail = kopisService.fetchVenueDetail(performanceDetail.getVenueId());
                // 공연장 저장 (중복 체크)
                venue = venueRepository.findByNameAndAddress(venueDetail.name(), venueDetail.address())
                        .orElseGet(() -> venueRepository.save(Venue.builder()
                                .name(venueDetail.name())
                                .address(venueDetail.address())
                                .build()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            // 중복 체크
            if (performanceRepository.existsByNameAndPoster(performanceDetail.getName(), performanceDetail.getPoster())) {
                continue;
            }

            // 공연 생성
            Performance performance = PerformanceMapper.toPerformance(performanceDetail, venue);

            // 공연 저장
            Performance savedPerformance = performanceRepository.save(performance);

            // 임시 게시글 생성
            postService.createPost(savedPerformance);
        }
    }
}
