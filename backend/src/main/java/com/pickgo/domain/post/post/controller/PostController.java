package com.pickgo.domain.post.post.controller;

import com.pickgo.domain.member.dto.MemberPrincipal;
import com.pickgo.domain.performance.entity.PerformanceType;
import com.pickgo.domain.post.post.dto.PostDetailResponse;
import com.pickgo.domain.post.post.dto.PostSimpleResponse;
import com.pickgo.domain.post.post.entity.PostSortType;
import com.pickgo.domain.post.post.service.PostService;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.response.RsData;
import com.pickgo.global.util.IdentifierResolver;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.pickgo.global.response.RsCode.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @Operation(
            summary = "게시글 목록 조회",
            description = "페이징, 제목 검색, 타입 필터링, 정렬 기능을 제공합니다."
    )
    @GetMapping()
    public RsData<PageResponse<PostSimpleResponse>> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) PerformanceType type,
            @RequestParam(defaultValue = "ID_DESC") PostSortType sort

    ) {
        return RsData.from(SUCCESS, postService.getPosts(page, size, keyword, type, sort));
    }

    @Operation(
            summary = "조회순 게시물 목록 조회",
            description = "개수 지정, 타입 필터링 기능을 제공합니다."
    )
    @GetMapping("/popular")
    public RsData<List<PostSimpleResponse>> getPopularPosts(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) PerformanceType type
    ) {
        return RsData.from(SUCCESS, postService.getPopularPosts(size, type));
    }

    @Operation(summary = "오픈 예정 게시물 목록 조회")
    @GetMapping("/opening-soon")
    public RsData<List<PostSimpleResponse>> getPopularPosts() {
        return RsData.from(SUCCESS, postService.getOpeningSoonPosts());
    }

    @Operation(summary = "게시물 상세 조회")
    @GetMapping("/{id}")
    public RsData<PostDetailResponse> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberPrincipal principal,
            HttpServletRequest request
    ) {
        // 게시물 상세 조회
        PostDetailResponse response = postService.getPost(id);
        // 조회수 증가
        String identifier = IdentifierResolver.resolve(principal, request);
        postService.increaseViewCount(identifier, id);

        return RsData.from(SUCCESS, response);
    }


}
