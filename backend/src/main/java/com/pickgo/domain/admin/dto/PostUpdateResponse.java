package com.pickgo.domain.admin.dto;

import com.pickgo.domain.post.entity.Post;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateResponse {
    private PostDetailResponse post;

    public static PostUpdateResponse from(Post post) {
        return PostUpdateResponse.builder()
                .post(PostDetailResponse.from(post))
                .build();
    }
}
