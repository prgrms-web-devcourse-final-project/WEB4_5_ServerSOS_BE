package com.pickgo.domain.auth.dto;

public record CreateTokenResponse(String accessToken) {
	public static CreateTokenResponse of(String accessToken) {
		return new CreateTokenResponse(accessToken);
	}
}
