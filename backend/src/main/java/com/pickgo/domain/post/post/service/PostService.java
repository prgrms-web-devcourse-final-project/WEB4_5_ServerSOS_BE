package com.pickgo.domain.post.post.service;

import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.entity.PerformanceType;
import com.pickgo.domain.post.post.dto.PostDetailResponse;
import com.pickgo.domain.post.post.dto.PostSimpleResponse;
import com.pickgo.domain.post.post.entity.Post;
import com.pickgo.domain.post.post.entity.PostSortType;
import com.pickgo.domain.post.post.repository.PostRepository;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void createPost(Performance performance) {
        Post post = Post.builder()
                .title(performance.getName())
                .content("임시 게시글 본문")
                .isPublished(false)
                .views(0L)
                .performance(performance)
                .build();

        postRepository.save(post);
    }

    @Transactional
    public void createPostPublished(Performance performance) {
        Post post = Post.builder()
                .title(performance.getName())
                .content("임시 게시글 본문")
                .isPublished(true)
                .views(0L)
                .performance(performance)
                .build();

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostSimpleResponse> getPosts(int page, int size, String keyword, PerformanceType type, PostSortType sort) {
        Pageable pageable = PageRequest.of(page - 1, size, sort.getSort());
        String formattedKeyword = keyword.toLowerCase().replaceAll(" ", "");

        Page<Post> posts = (type == null)
                ? postRepository.searchPostByTitle(pageable, formattedKeyword)
                : postRepository.searchPostByTitleAndType(pageable, formattedKeyword, type);

        return PageResponse.from(posts, PostSimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public List<PostSimpleResponse> getPopularPosts(int size, PerformanceType type) {
        List<Post> posts = (type == null)
                ? postRepository.findTopPostsByViews(size)
                : postRepository.findTopPostsByViewsAndType(size, type);

        return posts.stream().map(PostSimpleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PostSimpleResponse> getOpeningSoonPosts() {
        List<Post> posts = postRepository.findTopScheduledPosts();

        return posts.stream().map(PostSimpleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long id) {
        // performance, venue, performanceSession은 fetch join을 이용하고 performanceArea, performanceIntro는 batch를 이용한 lazy loading
        Post post = postRepository.findByIdWithAll(id)
                .orElseThrow(() -> new BusinessException(RsCode.POST_NOT_FOUND));
        return PostDetailResponse.from(post);
    }

    public void increaseViewCount(String identifier, Long id) {
        String viewedKey = "viewed:" + id + ":" + identifier;
        String viewCountKey = "view_count:" + id;

        // 조회 이력이 있다면 무시
        if (Boolean.TRUE.equals(redisTemplate.hasKey(viewedKey))) {
            return;
        }

        // 조회 기록 저장 (24시간 TTL)
        redisTemplate.opsForValue().set(viewedKey, "1", Duration.ofHours(24));

        // 조회수 증가
        redisTemplate.opsForValue().increment(viewCountKey);
    }
}
