package com.pickgo.jwt;

import static com.pickgo.global.response.RsCode.*;
import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import com.pickgo.global.jwt.JwtProvider;
import com.pickgo.token.TestToken;

@SpringBootTest
class JwtProviderTest {

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private TestToken token;

	@Test
	@DisplayName("유효한 토큰은 예외 없이 검증됨")
	void validateToken_success() {
		assertThatCode(() -> jwtProvider.validateToken(token.userToken))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("만료된 토큰은 예외 발생")
	void validateToken_fail_expired() {
		assertThatThrownBy(() -> jwtProvider.validateToken(token.expiredToken))
			.isInstanceOf(RuntimeException.class)
			.hasMessage(UNAUTHENTICATED.getMessage());
	}

	@Test
	@DisplayName("토큰으로부터 사용자 ID 추출 성공")
	void getUserId_success() {
		UUID userId = jwtProvider.getUserId(token.userToken);
		assertThat(userId).isNotNull();
	}

	@Test
	@DisplayName("토큰으로부터 인증 객체 생성 성공")
	void getAuthentication_success() {
		Authentication authentication = jwtProvider.getAuthentication(token.userToken);
		assertThat(authentication.getPrincipal()).isNotNull();
		assertThat(authentication.getAuthorities()).hasSize(1);
	}

	@Test
	@DisplayName("Authorization 헤더에서 Bearer 제거하고 액세스 토큰 추출")
	void getAccessToken_success() {
		String bearer = "Bearer " + token.userToken;
		String result = jwtProvider.getAccessToken(bearer);
		assertThat(result).isEqualTo(token.userToken);
	}

	@Test
	@DisplayName("Authorization 헤더가 null이면 예외 발생")
	void getAccessToken_fail_nullHeader() {
		assertThatThrownBy(() -> jwtProvider.getAccessToken(null))
			.isInstanceOf(RuntimeException.class)
			.hasMessage(UNAUTHENTICATED.getMessage());
	}

	@Test
	@DisplayName("Authorization 헤더에 Bearer 접두어가 없으면 예외 발생")
	void getAccessToken_fail_invalidPrefix() {
		assertThatThrownBy(() -> jwtProvider.getAccessToken("InvalidToken"))
			.isInstanceOf(RuntimeException.class)
			.hasMessage(UNAUTHENTICATED.getMessage());
	}
}
