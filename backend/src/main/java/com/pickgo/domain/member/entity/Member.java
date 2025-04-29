package com.pickgo.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "profile")
    private String profileImageUrl;

    @Column(name = "nickname")
    private String nickname;
}
