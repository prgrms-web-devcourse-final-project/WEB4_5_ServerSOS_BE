package com.pickgo.domain.member.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.entity.enums.Authority;
import com.pickgo.domain.member.entity.enums.SocialProvider;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberDetailResponse(
	UUID id,
	String email,
	String nickname,
	Authority authority,
	String profile,
	SocialProvider socialProvider,
	LocalDateTime createdAt,
	LocalDateTime modifiedAt
) {
	public static MemberDetailResponse from(Member member) {
		return new MemberDetailResponse(
			member.getId(),
			member.getEmail(),
			member.getNickname(),
			member.getAuthority(),
			member.getProfile(),
			member.getSocialProvider(),
			member.getCreatedAt(),
			member.getModifiedAt()
		);
	}
}
