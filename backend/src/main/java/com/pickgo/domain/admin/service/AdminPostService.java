package com.pickgo.domain.admin.service;

import com.pickgo.domain.admin.dto.PostCreateRequest;
import com.pickgo.domain.admin.dto.PostDetailResponse;
import com.pickgo.domain.admin.dto.PostSimpleResponse;
import com.pickgo.domain.admin.dto.PostUpdateRequest;
import com.pickgo.domain.admin.repository.AdminPostRepository;
import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.repository.PerformanceRepository;
import com.pickgo.domain.post.entity.Post;
import com.pickgo.domain.venue.entity.Venue;
import com.pickgo.domain.venue.repository.VenueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final AdminPostRepository adminPostRepository;
    private final PerformanceRepository performanceRepository;
    private final VenueRepository venueRepository;

    /*게시물 전체 목록 조회*/
    public List<PostSimpleResponse> getAllPosts() {
        List<Post> posts = adminPostRepository.findAllWithPerformance();

        return posts.stream()
                .map(PostSimpleResponse::from)
                .collect(Collectors.toList());
    }

    public PostDetailResponse getPostDetail(Long id) {
        Post post = adminPostRepository.findByIdWithPerformance(id)
                .orElseThrow(() -> new EntityNotFoundException("요청하신 게시글을 찾을 수 없습니다."));
        return PostDetailResponse.from(post);
    }

    /*게시글 작성*/
    public Long createPost(PostCreateRequest request) {

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new RuntimeException("공연장 정보를 찾을 수 없습니다."));

        Performance performance = Performance.builder()
                .venue(venue)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .poster(request.getPoster())
                .build();
        performanceRepository.save(performance);


        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isPublished(false)
                .performance(performance)
                .build();
        Post savedPost = adminPostRepository.save(post);

        return savedPost.getId();

    }
    /*게시글 수정*/
    @Transactional
    public void updatePost(Long id, PostUpdateRequest request) {
    Post post = adminPostRepository.findById(id)
            .orElseThrow(()-> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

    Performance performance = performanceRepository.findById(request.getPerformanceId())
            .orElseThrow(()-> new IllegalArgumentException("공연이 존재하지 않습니다."));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setIsPublished(request.getIsPublished());
        post.setPerformance(performance);
        post.setModifiedAt(LocalDateTime.now());
    }

    /*게시글 삭제*/
    @Transactional
    public void deletePost(Long id) {
    Post post = adminPostRepository.findById(id)
            .orElseThrow(()-> new EntityNotFoundException("게시글을 찾을 수 업습니다."));

    adminPostRepository.delete(post);
    }


}


