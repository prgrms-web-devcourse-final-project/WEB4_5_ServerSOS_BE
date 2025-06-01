package com.pickgo.domain.post.post.entity;

import java.util.ArrayList;
import java.util.List;

import com.pickgo.domain.performance.performance.entity.Performance;
import com.pickgo.domain.post.review.entity.Review;
import com.pickgo.global.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class Post extends BaseEntity {
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean isPublished;

    @Column(nullable = false)
    private Long views;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private final List<Review> reviews = new ArrayList<>();

    public void addReview(Review review) {
        reviews.add(review);
    }
}
