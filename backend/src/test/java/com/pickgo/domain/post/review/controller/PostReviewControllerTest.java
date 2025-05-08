package com.pickgo.domain.post.review.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.post.review.dto.PostReviewCreateRequest;
import com.pickgo.domain.post.review.dto.PostReviewSimpleResponse;
import com.pickgo.domain.post.review.dto.PostReviewUpdateRequest;
import com.pickgo.domain.post.review.service.PostReviewService;
import com.pickgo.global.response.RsCode;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PostReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostReviewService postReviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글 리뷰 목록 조회")
    void getReviews() throws Exception {
        // given
        Long postId = 1L;
        List<PostReviewSimpleResponse> mockReviews = List.of(
                PostReviewSimpleResponse.builder()
                        .reviewId(1L)
                        .userId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                        .profile("profile1.jpg")
                        .nickname("작성자1")
                        .content("좋아요")
                        .build(),
                PostReviewSimpleResponse.builder()
                        .reviewId(2L)
                        .userId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                        .profile("profile2.jpg")
                        .nickname("작성자2")
                        .content("별로에요")
                        .build()
        );

        Mockito.when(postReviewService.getReviewsByPostId(postId))
                .thenReturn(mockReviews);

        // when & then
        mockMvc.perform(get("/api/posts/{id}/reviews", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].content").value("좋아요"))
                .andExpect(jsonPath("$.data[1].content").value("별로에요"));
    }

    @Test
    @DisplayName("게시글 리뷰 작성")
    void createReview() throws Exception {
        // given
        Long postId = 1L;
        UUID memberId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        PostReviewCreateRequest request = new PostReviewCreateRequest(
                postId,
                "좋은 글입니다.",
                "작성자1",
                memberId,
                "2025-04-29T10:00:00"
        );

        PostReviewSimpleResponse mockResponse = PostReviewSimpleResponse.builder()
                .reviewId(1L)
                .userId(memberId)
                .profile("profile.jpg")
                .nickname("작성자1")
                .content("좋은 글입니다.")
                .build();

        Mockito.when(postReviewService.createReview(eq(postId), any(PostReviewCreateRequest.class)))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/posts/{id}/reviews", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(RsCode.REVIEW_CREATED.getCode()))
                .andExpect(jsonPath("$.data.content").value("좋은 글입니다."));
    }

    @Test
    @DisplayName("게시글 리뷰 수정")
    void updateReview() throws Exception {
        // given
        Long postId = 1L;
        Long reviewId = 1L;
        UUID memberId = UUID.fromString("44444444-4444-4444-4444-444444444444");

        PostReviewUpdateRequest request = new PostReviewUpdateRequest("수정된 리뷰 내용");

        PostReviewSimpleResponse mockResponse = PostReviewSimpleResponse.builder()
                .reviewId(reviewId)
                .userId(memberId)
                .profile("profile.jpg")
                .nickname("작성자1")
                .content("수정된 리뷰 내용")
                .build();

        Mockito.when(postReviewService.updateReview(eq(postId), eq(reviewId), any(PostReviewUpdateRequest.class)))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(put("/api/posts/{id}/reviews/{reviewId}", postId, reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.REVIEW_UPDATED.getCode()))
                .andExpect(jsonPath("$.data.content").value("수정된 리뷰 내용"));
    }

    @Test
    @DisplayName("게시글 리뷰 삭제")
    void deleteReview() throws Exception {
        // given
        Long postId = 1L;
        Long reviewId = 1L;

        Mockito.doNothing().when(postReviewService).deleteReview(postId, reviewId);

        // when & then
        mockMvc.perform(delete("/api/posts/{id}/reviews/{reviewId}", postId, reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.REVIEW_DELETED.getCode()))
                .andExpect(jsonPath("$.message").value("리뷰가 삭제되었습니다."));
    }
}
