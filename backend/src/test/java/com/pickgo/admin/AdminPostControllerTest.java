package com.pickgo.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.admin.dto.PostDetailResponse;
import com.pickgo.domain.admin.service.AdminPostService;
import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.entity.*;
import com.pickgo.domain.post.entity.Post;
import com.pickgo.domain.venue.entity.Venue;
import com.pickgo.global.response.RsCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
class AdminPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminPostService adminPostService;

    @MockBean
    private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글 전체 목록 조회 성공")
    void getPostList() throws Exception {
        // given
        List<Post> postEntities = List.of(
                Post.builder().id(1L).title("게시글1")
                        .performance(
                                Performance.builder()
                                        .poster("poster1.jpg")
                                        .endDate(LocalDate.now())
                                        .venue(Venue.builder().name("공연장1").build())
                                        .build()
                        ).build(),
                Post.builder().id(2L).title("게시글2")
                        .performance(
                                Performance.builder()
                                        .poster("poster2.jpg")
                                        .endDate(LocalDate.now())
                                        .venue(Venue.builder().name("공연장2").build())
                                        .build()
                        ).build()
        );

        Page<Post> postPage = new PageImpl<>(postEntities, PageRequest.of(0, 10), postEntities.size());

        // adminPostService는 Page<Post>를 반환하므로, 여기까지가 given
        when(adminPostService.getAllPosts(1, 10, true)).thenReturn(postPage);

        // when & then
        mockMvc.perform(get("/api/admin/posts")
                        .param("page", "1")
                        .param("size", "10")
                        .param("isPublished", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.items[0].id").value(1L))
                .andExpect(jsonPath("$.data.items[1].id").value(2L))
                .andDo(print());
    }



    @Test
    @DisplayName("PostDetailResponse 응답 변환 성공")
    void toResponse() {
        // given
        Venue venue = Venue.builder()
                .name("서울 공연장")
                .address("서울시 강남구")
                .build();

        PerformanceIntro intro1 = PerformanceIntro.builder()
                .intro_image("소개 이미지 url")
                .build();

        PerformanceArea area = PerformanceArea.builder()
                .id(1L)
                .name("VIP")
                .grade(null)  // null 값 테스트
                .price(100000)
                .build();

        PerformanceSession session = PerformanceSession.builder()
                .id(1L)
                .performanceTime(LocalDateTime.of(2025, 4, 27, 19, 0))
                .build();

        Performance performance = Performance.builder()
                .id(1L)
                .name("공연 이름")
                .poster("https://example.com/poster.jpg")
                .state(PerformanceState.SCHEDULED)
                .type(PerformanceType.MUSICAL)
                .casts("유재석, 강호동, 신동엽")
                .runtime("120")
                .minAge("10")
                .startDate(LocalDate.of(2025, 4, 29))
                .endDate(LocalDate.of(2025, 4, 30))

                .venue(venue)
                .performanceIntros(List.of(intro1, intro1, intro1))
                .performanceAreas(List.of(area))
                .performanceSessions(List.of(session))
                .build();

        Post post = Post.builder()
                .id(2L)
                .title("수정된 게시글 제목")
                .content("수정된 게시글 내용")
                .isPublished(true)
                .views(1000L)
                .createdAt(LocalDateTime.of(2025, 4, 24, 14, 30, 45))
                .modifiedAt(LocalDateTime.of(2025, 5, 2, 11, 55, 28))
                .performance(performance)
                .build();

        // when
        PostDetailResponse response = PostDetailResponse.from(post);

        // then
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getTitle()).isEqualTo("수정된 게시글 제목");
        assertThat(response.getPerformance().getName()).isEqualTo("공연 이름");
        assertThat(response.getPerformance().getVenue().name()).isEqualTo("서울 공연장");
        assertThat(response.getPerformance().getIntroImages()).hasSize(3);
        assertThat(response.getPerformance().getSessions()).hasSize(1);
    }


    @Test
    @DisplayName("게시글 삭제 성공")
    void deletePost() throws Exception {
        doNothing().when(adminPostService).deletePost(1L);

        mockMvc.perform(delete("/api/admin/posts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(1)) // ← 단순 값일 경우
                .andDo(print());
    }
}
