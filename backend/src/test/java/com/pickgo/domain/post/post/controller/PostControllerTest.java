package com.pickgo.domain.post.post.controller;

import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.entity.PerformanceState;
import com.pickgo.domain.performance.entity.PerformanceType;
import com.pickgo.domain.performance.repository.PerformanceRepository;
import com.pickgo.domain.post.post.dto.PostDetailResponse;
import com.pickgo.domain.post.post.dto.PostSimpleResponse;
import com.pickgo.domain.post.post.entity.Post;
import com.pickgo.domain.post.post.entity.PostSortType;
import com.pickgo.domain.post.post.repository.PostRepository;
import com.pickgo.domain.post.post.service.PostService;
import com.pickgo.domain.venue.entity.Venue;
import com.pickgo.global.dto.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @MockitoBean
    private PostService postService;

    private Performance performance;
    private Post post;

    @BeforeEach
    void setUp() {
        performance = Performance.builder()
                .name("공연명")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(10))
                .runtime("120분")
                .poster("poster.jpg")
                .state(PerformanceState.SCHEDULED)
                .minAge("전체 이용가")
                .casts("")
                .type(PerformanceType.MUSICAL)
                .venue(Venue.builder().name("공연장명").build())
                .build();
        performanceRepository.save(performance);

        post = Post.builder()
                .title("테스트 게시글")
                .content("테스트 본문")
                .performance(performance)
                .views(100L)
                .isPublished(true)
                .build();

        postRepository.save(post);
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    void getPosts() throws Exception {
        int page = 1, size = 10;
        String keyword = "";
        PerformanceType type = null;
        PostSortType sort = PostSortType.ID_DESC;

        when(postService.getPosts(page, size, keyword, type, sort))
                .thenReturn(PageResponse.<PostSimpleResponse>builder()
                        .items(List.of(PostSimpleResponse.from(post)))
                        .page(1)
                        .size(10)
                        .totalElements(1)
                        .totalPages(1)
                        .build());

        mockMvc.perform(get("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.items[0].title").value("테스트 게시글"))
                .andExpect(jsonPath("$.data.items[0].views").value(100));
    }

    @Test
    @DisplayName("인기 게시글 조회 테스트")
    void getPopularPosts() throws Exception {
        int size = 5;
        PerformanceType type = null;

        when(postService.getPopularPosts(size, type))
                .thenReturn(List.of(PostSimpleResponse.from(post)));

        mockMvc.perform(get("/api/posts/popular")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("테스트 게시글"))
                .andExpect(jsonPath("$.data[0].views").value(100));
    }

    @Test
    @DisplayName("오픈 예정 게시글 조회 테스트")
    void getOpeningSoonPosts() throws Exception {
        when(postService.getOpeningSoonPosts())
                .thenReturn(List.of(PostSimpleResponse.from(post)));

        mockMvc.perform(get("/api/posts/opening-soon")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("테스트 게시글"));
    }

    @Test
    @DisplayName("게시글 상세 조회 테스트")
    void getPost() throws Exception {
        Long id = 1L;

        when(postService.getPost(id))
                .thenReturn(PostDetailResponse.from(post));

        mockMvc.perform(get("/api/posts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.data.views").value(100))
                .andExpect(jsonPath("$.data.performance.name").value("공연명"))
                .andExpect(jsonPath("$.data.performance.poster").value("poster.jpg"))
                .andExpect(jsonPath("$.data.performance.type").value("뮤지컬"));
    }

}
