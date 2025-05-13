package com.pickgo.domain.member.dto;

import java.util.UUID;

import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.entity.enums.Authority;

import lombok.Builder;

@Builder
public record MemberSimpleResponse(
    UUID id,
    String email,
    String nickname,
    Authority authority,
    String createdAt
) {
    public static MemberSimpleResponse from(Member member) {
        return MemberSimpleResponse.builder()
            .id(member.getId())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .authority(member.getAuthority())
            .createdAt(member.getCreatedAt().toString())
            .build();
    }
}
