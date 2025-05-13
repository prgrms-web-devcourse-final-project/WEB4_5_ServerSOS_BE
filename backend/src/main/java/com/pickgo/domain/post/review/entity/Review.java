package com.pickgo.domain.post.review.entity;

import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.post.post.entity.Post;
import com.pickgo.global.entity.BaseEntity;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(indexes = @Index(name = "idx_like_count", columnList = "likeCount"))
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    @Setter
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;


    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void canAccess(Member actor) {
        if (!this.member.equals(actor)) {
            throw new BusinessException(RsCode.FORBIDDEN);
        }
    }
}
