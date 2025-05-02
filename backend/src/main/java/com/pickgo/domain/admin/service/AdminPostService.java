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

@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final AdminPostRepository adminPostRepository;
    private final PerformanceRepository performanceRepository;

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
    public void updatePost(Long id, PostUpdateRequest request) {
    Post post = adminPostRepository.findById(id)
            .orElseThrow(RsCode.POST_NOT_FOUND::toException);

    Performance performance = performanceRepository.findById(request.getPerformanceId())
            .orElseThrow(RsCode.PERFORMANCE_NOT_FOUND::toException);

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setIsPublished(request.getIsPublished());
        post.setPerformance(performance);
    }

    /*게시글 삭제*/
    @Transactional
    public void deletePost(Long id) {
    Post post = adminPostRepository.findById(id)
            .orElseThrow(RsCode.POST_NOT_FOUND::toException);

    adminPostRepository.delete(post);
    }


}


