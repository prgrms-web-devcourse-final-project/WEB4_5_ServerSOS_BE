package com.pickgo.domain.log.entity;

import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.enums.ActorType;
import com.pickgo.domain.log.enums.HttpMethod;
import com.pickgo.global.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
public class BusinessLog extends BaseEntity {

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
}
