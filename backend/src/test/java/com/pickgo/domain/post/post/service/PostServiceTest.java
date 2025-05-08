package com.pickgo.domain.post.post.service;

import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.entity.PerformanceState;
import com.pickgo.domain.performance.entity.PerformanceType;
import com.pickgo.domain.post.post.dto.PostDetailResponse;
import com.pickgo.domain.post.post.dto.PostSimpleResponse;
import com.pickgo.domain.post.post.entity.Post;
import com.pickgo.domain.post.post.entity.PostSortType;
import com.pickgo.domain.post.post.repository.PostRepository;
import com.pickgo.domain.venue.entity.Venue;
import com.pickgo.global.dto.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private PostService postService;

    private Post createPost() {
        Performance performance = Performance.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .poster("poster.jpg")
                .state(PerformanceState.SCHEDULED)
                .type(PerformanceType.MUSICAL)
                .venue(Venue.builder().build())
                .performanceIntros(List.of())
                .performanceAreas(List.of())
                .performanceSessions(List.of())
                .build();

        return Post.builder()
                .id(1L)
                .title("테스트 공연")
                .views(100L)
                .performance(performance)
                .build();
    }

    @Test
    @DisplayName("게시글 목록 조회 테스트")
    void getPosts() {
        // given
        String keyword = "";
        int page = 1, size = 10;
        PerformanceType type = null;
        PostSortType sort = PostSortType.ID_DESC;
        Pageable pageable = PageRequest.of(0, size, sort.getSort());

        Post post = createPost();
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 1);

        when(postRepository.searchPostByTitle(pageable, keyword)).thenReturn(postPage);

        // when
        PageResponse<PostSimpleResponse> result = postService.getPosts(page, size, keyword, type, sort);

        // then
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).title()).isEqualTo(post.getTitle());
    }

    @Test
    @DisplayName("조회순 게시물 목록 조회 테스트")
    void getPopularPosts() {
        // given
        int size = 5;
        List<Post> mockPosts = List.of(createPost(), createPost());
        when(postRepository.findTopPostsByViews(size)).thenReturn(mockPosts);

        // when
        List<PostSimpleResponse> result = postService.getPopularPosts(size, null);

        // then
        assertThat(result).hasSize(2);
        verify(postRepository).findTopPostsByViews(size);
    }

    @Test
    @DisplayName("오픈 임박 게시물 목록 조회 테스트")
    void getOpeningSoonPosts() {
        // given
        List<Post> scheduledPosts = List.of(createPost());
        when(postRepository.findTopScheduledPosts()).thenReturn(scheduledPosts);

        // when
        List<PostSimpleResponse> result = postService.getOpeningSoonPosts();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo(scheduledPosts.get(0).getTitle());
    }

    @Test
    @DisplayName("게시글 상세 조회 테스트")
    void getPost() {
        // given
        Long id = 1L;
        Post post = createPost();
        when(postRepository.findByIdWithAll(id)).thenReturn(Optional.of(post));

        // when
        PostDetailResponse result = postService.getPost(id);

        // then
        assertThat(result.id()).isEqualTo(post.getId());
        assertThat(result.title()).isEqualTo(post.getTitle());
        assertThat(result.views()).isEqualTo(post.getViews());
        assertThat(result.performance().name()).isEqualTo(post.getPerformance().getName());
        assertThat(result.performance().poster()).isEqualTo(post.getPerformance().getPoster());
        assertThat(result.performance().type()).isEqualTo(post.getPerformance().getType().getValue());
    }

    @Test
    @DisplayName("조회수 증가 테스트")
    void increaseViewCount_onlyOncePerIdentifier() {
        // given
        Long postId = 1L;
        String identifier = "MEMBER_1";

        String viewedKey = "viewed:" + postId + ":" + identifier;
        String viewCountKey = "view_count:" + postId;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey(viewedKey)).thenReturn(false); // 조회 이력이 없을 때
        when(valueOperations.increment(viewCountKey)).thenReturn(1L);

        // when
        postService.increaseViewCount(identifier, postId);

        // then
        verify(redisTemplate).hasKey(viewedKey); // 조회 이력 확인
        verify(valueOperations).set(viewedKey, "1", Duration.ofHours(24)); // 조회 기록 저장
        verify(valueOperations).increment(viewCountKey); // 조회수 증가
    }

    @Test
    @DisplayName("중복 조회는 조회수를 증가시키지 않는다")
    void increaseViewCount_duplicateIgnored() {
        // given
        Long postId = 1L;
        String identifier = "IP_127.0.0.1"; // 비회원 조회를 위한 IP 기반 식별자

        String viewedKey = "viewed:" + postId + ":" + identifier;
        String viewCountKey = "view_count:" + postId;

        // 이미 조회 이력이 있다고 가정
        when(redisTemplate.hasKey(viewedKey)).thenReturn(true); // 조회 이력 있음

        // when
        postService.increaseViewCount(identifier, postId);

        // then
        // 조회 이력이 있다는 조건이므로 조회수가 증가하지 않아야 한다
        verify(redisTemplate).hasKey(viewedKey); // 조회 이력 확인
        verify(redisTemplate, never()).opsForValue(); // opsForValue 호출되지 않음
        verify(valueOperations, never()).set(viewedKey, "1", Duration.ofHours(24)); // 조회 기록 저장 안 됨
        verify(valueOperations, never()).increment(viewCountKey); // 조회수 증가 안 됨
    }
}
