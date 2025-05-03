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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

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
}
