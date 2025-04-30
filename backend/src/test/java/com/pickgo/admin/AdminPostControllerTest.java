package com.pickgo.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.admin.dto.PostCreateRequest;
import com.pickgo.domain.admin.dto.PostDetailResponse;
import com.pickgo.domain.admin.dto.PostSimpleResponse;
import com.pickgo.domain.admin.dto.PostUpdateRequest;
import com.pickgo.domain.admin.service.AdminPostService;
import com.pickgo.global.response.RsCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
        when(adminPostService.getAllPosts()).thenReturn(
                List.of(
                        new PostSimpleResponse(1L, "게시글1", "공연장1", LocalDate.now(), LocalDate.now(), "poster1.jpg"),
                        new PostSimpleResponse(2L, "게시글2", "공연장2", LocalDate.now(), LocalDate.now(), "poster2.jpg")
                )
        );

        mockMvc.perform(get("/api/admin/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 상세 조회 성공")
    void getPostDetail() throws Exception {
        when(adminPostService.getPostDetail(anyLong())).thenReturn(
                new PostDetailResponse(
                        1L,
                        "게시글1",
                        "내용",
                        true,
                        10L,
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 2),
                        "공연장"
                )
        );

        mockMvc.perform(get("/api/admin/posts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("게시글1"))
                .andExpect(jsonPath("$.data.content").value("내용"))
                .andDo(print());
    }



    @Test
    @DisplayName("게시글 작성 성공")
    void createPost() throws Exception {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("새 게시글");
        request.setContent("내용입니다");

        when(adminPostService.createPost(any(PostCreateRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/api/admin/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(RsCode.CREATED.getCode()))
                .andExpect(jsonPath("$.data.id").value(1)) // ← 단순 Long 값이면 이렇게
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
