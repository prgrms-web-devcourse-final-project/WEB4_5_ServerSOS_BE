package com.pickgo.example.controller;

import com.pickgo.example.dto.PostCreateRequest;
import com.pickgo.example.dto.PostSimpleResponse;
import com.pickgo.example.service.AdminPostService;
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

    @Operation(summary = "admin 게시글 작성")
    @PostMapping
    public RsData<Long> createPost(@RequestBody PostCreateRequest request) {
        Long postId = adminPostService.createPost(request);
        return RsData.from(RsCode.CREATED, postId);
    }
}
