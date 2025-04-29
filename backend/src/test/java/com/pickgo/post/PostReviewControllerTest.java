package com.pickgo.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.post.dto.PostReviewCreateRequest;
import com.pickgo.domain.post.dto.PostReviewSimpleResponse;
import com.pickgo.domain.post.dto.PostReviewUpdateRequest;
import com.pickgo.domain.post.service.PostReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                        .userId(10L)
                        .profile("profile1.jpg")
                        .nickname("작성자1")
                        .content("좋아요")
                        .build(),
                PostReviewSimpleResponse.builder()
                        .reviewId(2L)
                        .userId(20L)
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

        PostReviewCreateRequest request = new PostReviewCreateRequest(
                postId,
                "좋은 글입니다.",
                "작성자1",
                10L,
                "2025-04-29T10:00:00"
        );

        PostReviewSimpleResponse mockResponse = PostReviewSimpleResponse.builder()
                .reviewId(1L)
                .userId(10L)
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
                .andExpect(status().isCreated())  // ✅ 201 Created
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.content").value("좋은 글입니다."));
    }

    @Test
    @DisplayName("게시글 리뷰 수정")
    void updateReview() throws Exception {
        // given
        Long postId = 1L;
        Long reviewId = 1L;

        PostReviewUpdateRequest request = new PostReviewUpdateRequest("수정된 리뷰 내용");

        PostReviewSimpleResponse mockResponse = PostReviewSimpleResponse.builder()
                .reviewId(reviewId)
                .userId(10L)
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
                .andExpect(jsonPath("$.code").value(200))
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
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("리뷰 삭제 완료"));
    }
}
