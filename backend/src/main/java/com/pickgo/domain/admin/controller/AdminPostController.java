package com.pickgo.domain.admin.controller;

import com.pickgo.domain.admin.dto.PostDetailResponse;
import com.pickgo.domain.admin.dto.PostSimpleResponse;
import com.pickgo.domain.admin.dto.PostUpdateRequest;
import com.pickgo.domain.admin.service.AdminPostService;
import com.pickgo.global.response.RsCode;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@Tag(name = "Admin API", description = "Admin API 엔드포인트")
public class AdminPostController {

    private final AdminPostService adminPostService;

    @Operation(summary = "admin 게시글 전체 목록 조회")
    @GetMapping
    public RsData<List<PostSimpleResponse>> getPostList() {
        List<PostSimpleResponse> responses = adminPostService.getAllPosts();
        return RsData.from(RsCode.SUCCESS,responses);
    }

    @Operation(summary = "admin 게시글 상세 조회")
    @GetMapping("/{id}")
    public RsData<PostDetailResponse> getPostDetail(@PathVariable("id") Long id) {
        PostDetailResponse response = adminPostService.getPostDetail(id);
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
