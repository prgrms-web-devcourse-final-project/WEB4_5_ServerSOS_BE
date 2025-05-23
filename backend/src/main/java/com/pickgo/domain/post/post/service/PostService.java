package com.pickgo.domain.post.post.service;

import com.pickgo.domain.performance.performance.entity.Performance;
import com.pickgo.domain.performance.performance.entity.PerformanceType;
import com.pickgo.domain.post.post.dto.PostDetailResponse;
import com.pickgo.domain.post.post.dto.PostSimpleResponse;
import com.pickgo.domain.post.post.entity.Post;
import com.pickgo.domain.post.post.entity.PostSortType;
import com.pickgo.domain.post.post.repository.PostRepository;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.PageResponse;
import com.pickgo.global.response.RsCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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

    private final String CONTENT = """
            공연 관람 전 안내사항
            
            안녕하세요. 관객 여러분의 안전하고 쾌적한 공연 관람을 위해 아래 사항을 꼭 확인해주시기 바랍니다.
            
            1. 티켓 수령 및 입장
            - 공연 시작 30분 전까지 티켓 수령을 완료해주시기 바랍니다.
            - 공연 시작 후에는 입장이 제한될 수 있으니, 시간 엄수 부탁드립니다.
            
            2. 촬영 및 녹음 금지
            - 공연 중 사진 촬영, 영상 녹화, 음성 녹음은 엄격히 금지되어 있습니다.
            - 위반 시 퇴장 조치될 수 있으니 유의 부탁드립니다.
            
            3. 공연 중 에티켓
            - 공연 중 휴대폰은 반드시 전원을 꺼주시거나 무음 모드로 설정해 주세요.
            - 다른 관객의 관람에 방해가 되지 않도록 조용한 관람을 부탁드립니다.
            
            4. 음식물 반입 금지
            - 공연장 내 음식물 반입은 금지되어 있으며, 물 이외의 음료는 삼가주시기 바랍니다.
            
            5. 유아 및 어린이 관람
            - 공연에 따라 입장 연령이 제한될 수 있으며, 사전에 확인 부탁드립니다.
            
            본 공연에 많은 관심과 성원 부탁드리며, 즐거운 관람 되시길 바랍니다.
            감사합니다.
            """;

    @Transactional
    public void createPost(Performance performance) {
        Post post = Post.builder()
                .title(performance.getName())
                .content(CONTENT)
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
                .content(CONTENT)
                .isPublished(true)
                .views(0L)
                .performance(performance)
                .build();

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "posts", key = "'page:' + #page + ':size:' + #size + ':keyword:' + #keyword + ':type:' + #type + ':sort:' + #sort")
    public PageResponse<PostSimpleResponse> getPosts(int page, int size, String keyword, PerformanceType type, PostSortType sort) {
        Pageable pageable = PageRequest.of(page - 1, size, sort.getSort());
        String formattedKeyword = keyword.toLowerCase().replaceAll(" ", "");

        Page<Post> posts = (type == null)
                ? postRepository.searchPostByTitle(pageable, formattedKeyword)
                : postRepository.searchPostByTitleAndType(pageable, formattedKeyword, type);

        return PageResponse.from(posts, PostSimpleResponse::from);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "popularPosts", key = "'size:' + #size + ':type:' + #type")
    public List<PostSimpleResponse> getPopularPosts(int size, PerformanceType type) {
        List<Post> posts = (type == null)
                ? postRepository.findTopPostsByViews(size)
                : postRepository.findTopPostsByViewsAndType(size, type);

        return posts.stream().map(PostSimpleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "openingSoonPosts")
    public List<PostSimpleResponse> getOpeningSoonPosts() {
        List<Post> posts = postRepository.findTopScheduledPosts();

        return posts.stream().map(PostSimpleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "post", key = "#id")
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
