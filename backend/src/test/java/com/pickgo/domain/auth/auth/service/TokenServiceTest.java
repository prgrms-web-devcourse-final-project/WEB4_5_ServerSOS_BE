package com.pickgo.domain.auth.auth.service;

import static com.pickgo.domain.member.member.entity.enums.Authority.*;
import static com.pickgo.global.response.RsCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pickgo.domain.auth.token.dto.TokenDetailResponse;
import com.pickgo.domain.auth.token.service.TokenService;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.member.member.repository.MemberRepository;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.jwt.JwtProvider;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private TokenService tokenService;

	@Mock
	private HttpServletResponse response;

	private final String refreshToken = "refresh-token";
	private final String accessToken = "access-token";

	@Test
	@DisplayName("refreshToken 으로 accessToken 발급 성공")
	void createAccessToken_success() {
		// given
		Member mockMember = getMockMember();
		Map<String, Object> claims = createClaims(mockMember);

		given(jwtProvider.getUserId(refreshToken)).willReturn(mockMember.getId());
		given(memberRepository.findById(mockMember.getId())).willReturn(Optional.of(mockMember));
		given(jwtProvider.generateToken(eq(mockMember.getId().toString()), any(), eq(claims))).willReturn(accessToken);
		given(jwtProvider.isValidToken(refreshToken)).willReturn(true);

		// when
		TokenDetailResponse response = tokenService.createAccessToken(refreshToken);

		// then
		assertThat(response.accessToken()).isEqualTo(accessToken);
	}

	@Test
	@DisplayName("refreshToken 으로 accessToken 발급 실패 - 존재하지 않는 유저")
	void createAccessToken_fail_memberNotFound() {
		// given
		UUID userId = UUID.randomUUID();

		given(jwtProvider.getUserId(refreshToken)).willReturn(userId);
		given(memberRepository.findById(userId)).willReturn(Optional.empty());
		given(jwtProvider.isValidToken(refreshToken)).willReturn(true);

		// when & then
		assertThatThrownBy(() -> tokenService.createAccessToken(refreshToken))
			.isInstanceOf(BusinessException.class)
			.hasMessage(NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("createRefreshToken()은 refreshToken을 생성하고 쿠키로 설정한다")
	void createRefreshToken_setsCookieSuccessfully() {
		// given
		Member mockMember = getMockMember();
		Map<String, Object> claims = createClaims(mockMember);

		given(jwtProvider.generateToken(eq(mockMember.getId().toString()), any(), eq(claims))).willReturn(refreshToken);

		// when
		tokenService.createRefreshToken(mockMember, response);

		// then
		ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

		verify(response).setHeader(headerCaptor.capture(), valueCaptor.capture());

		assertThat(headerCaptor.getValue()).isEqualTo("Set-Cookie");
		assertThat(valueCaptor.getValue()).contains("refreshToken=" + refreshToken);
		assertThat(valueCaptor.getValue()).contains("HttpOnly");
		assertThat(valueCaptor.getValue()).contains("Path=/");
		assertThat(valueCaptor.getValue()).contains("Max-Age=");
	}

	private Member getMockMember() {
		return Member.builder()
				.id(UUID.randomUUID())
				.email("test@example.com")
				.password("1234")
				.nickname("tester")
				.authority(USER)
				.build();
	}

	private Map<String, Object> createClaims(Member member) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", member.getId().toString());
		claims.put("authority", member.getAuthority().toString());
		return claims;
	}
}
