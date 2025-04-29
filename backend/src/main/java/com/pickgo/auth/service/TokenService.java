package com.pickgo.auth.service;

import static com.pickgo.global.response.RsCode.*;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pickgo.auth.dto.CreateTokenResponse;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.jwt.JwtProvider;
import com.pickgo.member.entity.Member;
import com.pickgo.member.repository.MemberRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {

	@Value("${custom.jwt.access_token.expiration_minutes}")
	private long accessTokenExpirationMinutes;

	@Value("${custom.jwt.refresh_token.expiration_minutes}")
	private long refreshTokenExpirationMinutes;

	@Value("${custom.http.secure}")
	private boolean secure;

	private final JwtProvider jwtProvider;
	private final MemberRepository memberRepository;

	public CreateTokenResponse createAccessToken(String refreshToken) {
		jwtProvider.validateToken(refreshToken);

		UUID userId = jwtProvider.getUserId(refreshToken);
		Member member = memberRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(NOT_FOUND));

		return CreateTokenResponse.of(genAccessToken(member));
	}

	public void createRefreshToken(Member member, HttpServletResponse response) {
		String newRefreshToken = genRefreshToken(member);
		addRefreshTokenCookie(response, newRefreshToken);
	}

	public String genAccessToken(Member member) {
		return jwtProvider.generateToken(member, Duration.ofMinutes(accessTokenExpirationMinutes));
	}

	public String genRefreshToken(Member member) {
		return jwtProvider.generateToken(member, Duration.ofMinutes(refreshTokenExpirationMinutes));
	}

	private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
			.httpOnly(true)
			.secure(secure)
			.path("/")
			.sameSite("None")
			.maxAge(Duration.ofMinutes(refreshTokenExpirationMinutes))
			.build();

		response.setHeader("Set-Cookie", cookie.toString());
	}

	public void removeRefreshTokenCookie(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
			.httpOnly(true)
			.secure(secure)
			.path("/")
			.sameSite("None")
			.maxAge(0)
			.build();

		response.setHeader("Set-Cookie", cookie.toString());
	}
}