package com.pickgo.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.post.review.dto.PostReviewCreateRequest;
import com.pickgo.domain.post.review.dto.PostReviewSimpleResponse;
import com.pickgo.domain.post.review.dto.PostReviewUpdateRequest;
import com.pickgo.domain.post.review.service.PostReviewService;
import com.pickgo.global.response.RsCode;
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
import java.util.UUID;

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
    @DisplayName("커서 기반 페이징으로 리뷰 목록 조회 - 최신순 정렬(id 내림차순)")
    void getReviewsWithCursorSortedById() throws Exception {
        // given
        Long postId = 1L;
        Long cursorId = 98L;
        int size = 10;
        String sort = "latest";

        List<PostReviewSimpleResponse> mockReviews = List.of(
                PostReviewSimpleResponse.builder()
                        .reviewId(97L)
                        .userId(UUID.randomUUID())
                        .profile("profile1.jpg")
                        .nickname("작성자1")
                        .content("좋아요")
                        .build(),
                PostReviewSimpleResponse.builder()
                        .reviewId(96L)
                        .userId(UUID.randomUUID())
                        .profile("profile2.jpg")
                        .nickname("작성자2")
                        .content("별로에요")
                        .build()
        );

        Mockito.when(postReviewService.getReviewsByPostId(eq(postId), eq(cursorId), eq(Integer.MAX_VALUE), eq(size), eq(sort)))
                .thenReturn(mockReviews);

        // when & then
        mockMvc.perform(get("/api/posts/{id}/reviews", postId)
                        .param("cursorId", cursorId.toString())
                        .param("size", String.valueOf(size))
                        .param("sort", sort))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].reviewId").value(97))
                .andExpect(jsonPath("$.data[1].reviewId").value(96));
    }

    @Test
    @DisplayName("커서 기반 페이징으로 리뷰 목록 조회 - 좋아요순 정렬(likeCount 내림차순 + id 내림차순)")
    void getReviewsWithCursorSortedByLikeCountDesc() throws Exception {
        // given
        Long postId = 1L;
        Long cursorId = 99L;
        int cursorLikeCount = 15;
        int size = 10;
        String sort = "like";

        List<PostReviewSimpleResponse> mockReviews = List.of(
                PostReviewSimpleResponse.builder()
                        .reviewId(96L)
                        .userId(UUID.randomUUID())
                        .profile("profile1.jpg")
                        .nickname("작성자1")
                        .content("좋아요")
                        .likeCount(24)
                        .build(),
                PostReviewSimpleResponse.builder()
                        .reviewId(98L)
                        .userId(UUID.randomUUID())
                        .profile("profile3.jpg")
                        .nickname("작성자3")
                        .content("별로에요22")
                        .likeCount(13)
                        .build(),
                PostReviewSimpleResponse.builder()
                        .reviewId(97L)
                        .userId(UUID.randomUUID())
                        .profile("profile2.jpg")
                        .nickname("작성자2")
                        .content("별로에요")
                        .likeCount(13)
                        .build()
        );

        Mockito.when(postReviewService.getReviewsByPostId(eq(postId), eq(cursorId), eq(cursorLikeCount), eq(size), eq(sort)))
                .thenReturn(mockReviews);

        // when & then
        mockMvc.perform(get("/api/posts/{id}/reviews", postId)
                        .param("cursorId", cursorId.toString())
                        .param("cursorLikeCount", String.valueOf(cursorLikeCount))
                        .param("size", String.valueOf(size))
                        .param("sort", sort))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].likeCount").value(24))
                .andExpect(jsonPath("$.data[1].likeCount").value(13))
                .andExpect(jsonPath("$.data[1].reviewId").value(98));
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

    @Test
    @DisplayName("리뷰 좋아요")
    void likeReview() throws Exception {
        // given
        Long postId = 1L;
        Long reviewId = 1L;

        Mockito.doNothing().when(postReviewService).likeReview(postId, reviewId);

        // when & then
        mockMvc.perform(post("/api/posts/{id}/reviews/{reviewId}/like", postId, reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value("리뷰에 좋아요를 추가했습니다."));
    }

    @Test
    @DisplayName("리뷰 좋아요 취소")
    void unlikeReview() throws Exception {
        // given
        Long postId = 1L;
        Long reviewId = 1L;

        Mockito.doNothing().when(postReviewService).cancelLikeReview(postId, reviewId);

        // when & then
        mockMvc.perform(delete("/api/posts/{id}/reviews/{reviewId}/like", postId, reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value("리뷰 좋아요를 취소했습니다."));
    }
}
