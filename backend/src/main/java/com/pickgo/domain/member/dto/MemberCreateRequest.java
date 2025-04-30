package com.pickgo.domain.member.dto;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.entity.enums.Authority;
import com.pickgo.domain.member.entity.enums.SocialProvider;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberCreateRequest(
	@NotBlank @Email String email,
	@NotBlank String password,
	@NotBlank String nickname
) {
	public Member toEntity(PasswordEncoder passwordEncoder, String profile) {
		return Member.builder()
			.id(UUID.randomUUID())
			.email(email)
			.password(passwordEncoder.encode(password)) // 암호화한 비밀번호 저장
			.nickname(nickname)
			.authority(Authority.USER)
			.profile(profile)
			.socialProvider(SocialProvider.NONE)
			.build();
	}
}
