package com.pickgo.domain.admin.service;

import com.pickgo.domain.admin.dto.PostUpdateRequest;
import com.pickgo.domain.admin.repository.AdminPostRepository;
import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.repository.PerformanceRepository;
import com.pickgo.domain.post.entity.Post;
import com.pickgo.global.response.RsCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final AdminPostRepository adminPostRepository;
    private final PerformanceRepository performanceRepository;

    /*게시물 전체 목록 조회*/
    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(int page, int size, Boolean isPublished) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts;

        if (isPublished != null) {
            posts = adminPostRepository.findAllWithPerformanceAndVenueByIsPublished(isPublished, pageable);
        } else {
            posts = adminPostRepository.findAllWithPerformanceAndVenue(pageable);
        }

        // Lazy 컬렉션 강제 초기화 (LazyInitializationException 방지)
        posts.forEach(post -> {
            Performance perf = post.getPerformance();
            if (perf != null) {
                perf.getPerformanceAreas().size();  // 강제 초기화
                perf.getPerformanceIntros().size(); // 강제 초기화
            }
        });

        return posts;
    }


    /*게시글 수정*/
    @Transactional(readOnly = true)
    public Post updatePost(Long id, PostUpdateRequest request) {
        Post post = adminPostRepository.findWithPerformance(id)
                .orElseThrow(RsCode.POST_NOT_FOUND::toException);

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setIsPublished(request.getIsPublished());

        List<PostUpdateRequest.PerformanceUpdateRequest.AreaPriceUpdateRequest> areaRequests =
                request.getPerformance().getAreas();

        if (areaRequests != null && !areaRequests.isEmpty()) {
            Map<Long, Integer> priceMap = areaRequests.stream()
                    .collect(Collectors.toMap(
                            PostUpdateRequest.PerformanceUpdateRequest.AreaPriceUpdateRequest::getId,
                            PostUpdateRequest.PerformanceUpdateRequest.AreaPriceUpdateRequest::getPrice
                    ));

            post.getPerformance().getPerformanceAreas().forEach(area -> {
                if (priceMap.containsKey(area.getId())) {
                    area.setPrice(priceMap.get(area.getId()));
                }
            });
        }

        return post;
    }

    /*게시글 삭제*/
    @Transactional
    public void deletePost(Long id) {
    Post post = adminPostRepository.findById(id)
            .orElseThrow(RsCode.POST_NOT_FOUND::toException);

    adminPostRepository.delete(post);
    }


}


