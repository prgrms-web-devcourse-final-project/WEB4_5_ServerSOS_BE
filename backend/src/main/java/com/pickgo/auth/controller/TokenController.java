package com.pickgo.auth.controller;

import static com.pickgo.global.response.RsCode.*;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pickgo.auth.dto.CreateTokenResponse;
import com.pickgo.auth.service.TokenService;
import com.pickgo.global.response.RsData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/tokens")
@RequiredArgsConstructor
@Tag(name = "Token API", description = "Token API 엔드포인트")
public class TokenController {

	private final TokenService tokenService;

	@Operation(summary = "액세스 토큰 발급")
	@PostMapping
	public RsData<CreateTokenResponse> createToken(@CookieValue(value = "refreshToken") String refreshToken) {
		return RsData.from(CREATED, tokenService.createAccessToken(refreshToken));
	}
}
