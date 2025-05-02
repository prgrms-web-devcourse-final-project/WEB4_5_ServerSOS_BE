package com.pickgo.domain.admin.service;

import com.pickgo.domain.admin.dto.PostUpdateRequest;
import com.pickgo.domain.admin.repository.AdminPostRepository;
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

    /*게시물 전체 목록 조회*/
    public Page<Post> getAllPosts(int page, int size, Boolean isPublished) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (isPublished != null) {
            return adminPostRepository.findAllWithPerformanceAndVenueByIsPublished(isPublished, pageable);
        }

        return adminPostRepository.findAllWithPerformanceAndVenue(pageable);
    }

    /*게시글 수정*/
    @Transactional
    public Post updatePost(Long id, PostUpdateRequest request) {
        Post post = adminPostRepository.findById(id)
                .orElseThrow(RsCode.POST_NOT_FOUND::toException);

        // 게시글 정보 수정 (제목, 내용, 게시 여부)
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setIsPublished(request.getIsPublished());

        // 좌석 구역 가격 수정
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


