package com.pickgo.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.admin.dto.PostUpdateRequest;
import com.pickgo.domain.admin.service.AdminPostService;
import com.pickgo.domain.performance.entity.Performance;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @DisplayName("게시글 수정 성공")
    void updatePost() throws Exception {
        Long performanceId = 1L;
        PostUpdateRequest request = new PostUpdateRequest();
        request.setPerformanceId(performanceId);
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");
        request.setIsPublished(true);

        doNothing().when(adminPostService).updatePost(eq(1L), any(PostUpdateRequest.class));

        mockMvc.perform(put("/api/admin/posts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andDo(print());
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
