package com.pickgo.domain.member.entity;

import java.util.UUID;

import com.pickgo.domain.member.entity.enums.Authority;
import com.pickgo.domain.member.entity.enums.SocialProvider;
import com.pickgo.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Builder
@Table(name = "member")
public class Member extends BaseEntity {

	@Id
	@Column(updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false)
	private String email;

	@Setter
	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Authority authority;

	@Setter
	private String profile;

	@Setter
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SocialProvider socialProvider;

	public void update(String nickname) {
		this.nickname = nickname;
	}
}
