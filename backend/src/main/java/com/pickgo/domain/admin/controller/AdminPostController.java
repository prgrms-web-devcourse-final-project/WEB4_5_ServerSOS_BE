package com.pickgo.domain.admin.controller;

import com.pickgo.domain.admin.dto.PostSimpleResponse;
import com.pickgo.domain.admin.dto.PostUpdateRequest;
import com.pickgo.domain.admin.service.AdminPostService;
import com.pickgo.domain.post.entity.Post;
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
@Tag(name = "Admin API", description = "Admin API 엔드포인트")
public class AdminPostController {

    private final AdminPostService adminPostService;

    @Operation(summary = "admin 게시글 페이징 목록 조회")
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

    @Operation(summary = "admin 게시글 수정")
    @PutMapping("/{id}")
    public RsData<PostUpdateRequest> updatePost(
            @PathVariable("id") Long id,
            @RequestBody PostUpdateRequest request
    ){
        adminPostService.updatePost(id,request);
        return RsData.from(RsCode.SUCCESS,request);
    }

    @Operation(summary = "admin 게시글 삭제")
    @DeleteMapping("/{id}")
    public RsData<Long> deletePost(
            @PathVariable("id") Long id
    ) {
        adminPostService.deletePost(id);
        return RsData.from(RsCode.SUCCESS,id);
    }
}
