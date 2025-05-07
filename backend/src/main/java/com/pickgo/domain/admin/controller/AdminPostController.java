package com.pickgo.domain.admin.controller;

import com.pickgo.domain.admin.dto.PostUpdateResponse;
import com.pickgo.domain.admin.dto.PostSimpleResponse;
import com.pickgo.domain.admin.dto.PostUpdateRequest;
import com.pickgo.domain.admin.service.AdminPostService;
import com.pickgo.domain.admin.dto.PostDetailResponse;
import com.pickgo.domain.post.post.entity.Post;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.response.RsCode;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin API", description = "관리자 전용 게시글 관리 API")
public class AdminPostController {

    private final AdminPostService adminPostService;

    @Operation(
            summary = "게시글 목록 조회 (관리자용)",
            description = "전체 게시글을 페이지 단위로 조회합니다. 게시 여부 필터링도 가능합니다."
    )
    @GetMapping
    public RsData<PageResponse<PostSimpleResponse>> getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isPublished
    ) {
        Page<Post> postPage = adminPostService.getAllPosts(page, size, isPublished);
        PageResponse<PostSimpleResponse> response = PageResponse.from(postPage, PostSimpleResponse::from);
        return RsData.from(RsCode.SUCCESS, response);
    }

    @Operation(
            summary = "게시글 수정 (관리자용)",
            description = "게시글 제목, 내용, 게시 여부 및 좌석 구역 가격 정보를 수정합니다."
    )
    @PutMapping("/{id}")
    public RsData<PostUpdateResponse> updatePost(
            @PathVariable("id") Long id,
            @RequestBody PostUpdateRequest request
    ) {
        Post updatedPost = adminPostService.updatePost(id, request);
        return RsData.from(RsCode.SUCCESS, new PostUpdateResponse(PostDetailResponse.from(updatedPost)));
    }

    @Operation(
            summary = "게시글 삭제 (관리자용)",
            description = "해당 ID의 게시글을 삭제합니다."
    )
    @DeleteMapping("/{id}")
    public RsData<Long> deletePost(
            @PathVariable("id") Long id
    ) {
        adminPostService.deletePost(id);
        return RsData.from(RsCode.SUCCESS, id);
    }
}

