package com.pickgo.domain.admin.service;

import com.pickgo.domain.admin.dto.PostDetailResponse;
import com.pickgo.domain.admin.dto.PostSimpleResponse;
import com.pickgo.domain.admin.dto.PostUpdateRequest;
import com.pickgo.domain.admin.repository.AdminPostRepository;
import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.repository.PerformanceRepository;
import com.pickgo.domain.post.entity.Post;
import com.pickgo.domain.venue.repository.VenueRepository;
import com.pickgo.global.response.RsCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final AdminPostRepository adminPostRepository;
    private final PerformanceRepository performanceRepository;
    private final VenueRepository venueRepository;

    /*게시물 전체 목록 조회*/
    public List<PostSimpleResponse> getAllPosts(Boolean isPublished) {
        List<Post> posts;

              if(isPublished ==null) {
                  posts = adminPostRepository.findAll(); //전체 조회
              }else{
                  posts = adminPostRepository.findByIsPublished(isPublished); //상태별 필터링
              }

        return posts.stream()
                .map(PostSimpleResponse::from)
                .collect(Collectors.toList());
    }

    public PostDetailResponse getPostDetail(Long id) {
        Post post = adminPostRepository.findByIdWithPerformance(id)
                .orElseThrow(RsCode.POST_NOT_FOUND::toException);
        return PostDetailResponse.from(post);
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


