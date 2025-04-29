package com.pickgo.member.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.pickgo.global.entity.BaseEntity;
import com.pickgo.member.entity.enums.Authority;
import com.pickgo.member.entity.enums.SocialProvider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
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
	@GeneratedValue
	@UuidGenerator
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

	private String profile;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SocialProvider socialProvider;
}
