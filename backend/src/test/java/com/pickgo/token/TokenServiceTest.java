package com.pickgo.token;

import static com.pickgo.global.response.RsCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pickgo.domain.auth.dto.TokenDetailResponse;
import com.pickgo.domain.auth.service.TokenService;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
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
		UUID userId = UUID.randomUUID();
		Member mockMember = mock(Member.class);

		given(jwtProvider.getUserId(refreshToken)).willReturn(userId);
		given(memberRepository.findById(userId)).willReturn(Optional.of(mockMember));
		given(jwtProvider.generateToken(eq(mockMember), any())).willReturn(accessToken);

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

		// when & then
		assertThatThrownBy(() -> tokenService.createAccessToken(refreshToken))
			.isInstanceOf(BusinessException.class)
			.hasMessage(NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("createRefreshToken()은 refreshToken을 생성하고 쿠키로 설정한다")
	void createRefreshToken_setsCookieSuccessfully() {
		// given
		Member member = mock(Member.class);

		given(jwtProvider.generateToken(eq(member), any(Duration.class)))
			.willReturn(refreshToken);

		// when
		tokenService.createRefreshToken(member, response);

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
}
