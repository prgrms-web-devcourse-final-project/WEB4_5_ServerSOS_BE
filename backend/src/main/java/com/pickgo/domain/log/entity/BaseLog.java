package com.pickgo.domain.log.entity;

import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.enums.ActorType;
import com.pickgo.domain.log.enums.HttpMethod;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@MappedSuperclass
public abstract class BaseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // actor -> 누구가 했는지
    @Column(nullable = false)
    private String actorId; // memberId or IP

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActorType actorType;

    // 어떤 행동을 했는지
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType action;

    // 누구에게
    @Column
    private String target;

    @Column
    private String targetName;

    @Column(nullable = false)
    private String requestUri; // api

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HttpMethod httpMethod; // POST, GET

    @Column(length = 1000)
    private String description; // 설명

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시'")
    private LocalDateTime createdAt;

}
