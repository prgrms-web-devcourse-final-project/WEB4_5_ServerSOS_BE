package com.pickgo.domain.post.service;

import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.post.entity.Post;
import com.pickgo.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public void createPost(Performance performance) {
        Post post = Post.builder()
                .title("게시글: " + performance.getName())
                .content("임시 게시글 본문")
                .isPublished(false)
                .views(0L)
                .performance(performance)
                .build();

        postRepository.save(post);
    }
}
