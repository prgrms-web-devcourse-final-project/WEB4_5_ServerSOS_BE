package com.pickgo.domain.member.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.log.repository.MemberHistoryRepository;
import com.pickgo.domain.member.admin.service.AdminPostService;
import com.pickgo.domain.performance.area.area.entity.AreaGrade;
import com.pickgo.domain.performance.area.area.entity.AreaName;
import com.pickgo.domain.performance.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.performance.entity.*;
import com.pickgo.domain.performance.venue.entity.Venue;
import com.pickgo.domain.post.post.dto.PostDetailResponse;
import com.pickgo.domain.post.post.entity.Post;
import com.pickgo.global.logging.service.HistorySaveService;
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
    private HistorySaveService historySaveService;

    @Autowired
    private MemberHistoryRepository memberHistoryRepository;

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
    @DisplayName("PostDetailResponse 응답 변환 성공 - 모든 필드 정상값")
    void toResponseWithoutNulls() {
        // given
        Venue venue = Venue.builder()
                .name("서울 공연장")
                .address("서울시 강남구")
                .build();

        PerformanceIntro intro1 = PerformanceIntro.builder()
                .introImage("소개 이미지 1")
                .build();

        PerformanceArea area = PerformanceArea.builder()
                .id(1L)
                .name(AreaName.VIP)
                .grade(AreaGrade.NORMAL)
                .price(100000)
                .build();

        PerformanceSession session = PerformanceSession.builder()
                .id(1L)
                .performanceTime(LocalDateTime.of(2025, 4, 27, 19, 0))
                .build();

        Performance performance = Performance.builder()
                .id(1L)
                .name("뮤지컬 헤드윅")
                .poster("https://example.com/poster.jpg")
                .state(PerformanceState.SCHEDULED)
                .type(PerformanceType.MUSICAL)
                .casts("조승우, 홍광호")
                .runtime("150")
                .minAge("12")
                .startDate(LocalDate.of(2025, 4, 29))
                .endDate(LocalDate.of(2025, 4, 30))
                .venue(venue)
                .performanceIntros(List.of(intro1, intro1))
                .performanceAreas(List.of(area))
                .performanceSessions(List.of(session))
                .build();

        Post post = Post.builder()
                .id(10L)
                .title("예매 안내")
                .content("공연 예매 방법 안내드립니다.")
                .isPublished(true)
                .views(555L)
                .performance(performance)
                .build();

        // when
        PostDetailResponse response = PostDetailResponse.from(post);

        // then
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("예매 안내");
        assertThat(response.performance().name()).isEqualTo("뮤지컬 헤드윅");
        assertThat(response.performance().state()).isEqualTo("공연예정");
        assertThat(response.performance().type()).isEqualTo("뮤지컬");
        assertThat(response.performance().runtime()).isEqualTo("150");
        assertThat(response.performance().minAge()).isEqualTo("12");
        assertThat(response.performance().venue().name()).isEqualTo("서울 공연장");
        assertThat(response.performance().images()).hasSize(2);
        assertThat(response.performance().areas()).hasSize(1);
        assertThat(response.performance().sessions()).hasSize(1);
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
