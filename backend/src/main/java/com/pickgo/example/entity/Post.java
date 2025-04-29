package com.pickgo.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performanceId;

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String content; // 내용

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished; //게시 여부

    @Column(name = "views")
    private Long views; //조회수

    @Column(name = "created_at")
    private LocalDateTime createdAt; //생성

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt; //수정


}
