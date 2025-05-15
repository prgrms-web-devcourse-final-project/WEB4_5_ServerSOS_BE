package com.pickgo.global.jwt;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import com.pickgo.global.token.TestToken;

@SpringBootTest
class JwtProviderTest {

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private TestToken token;

	@Test
	@DisplayName("유효한 토큰은 예외 없이 검증됨")
	void isValidToken_success() {
		assertThatCode(() -> jwtProvider.isValidToken(token.userToken))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("만료된 토큰은 예외 발생")
	void isValidToken_fail_expired() {
		assertThat(jwtProvider.isValidToken(token.expiredToken)).isFalse();
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
	void getToken_FromHeader_success() {
		String bearer = "Bearer " + token.userToken;
		String result = jwtProvider.getTokenFromHeader(bearer);
		assertThat(result).isEqualTo(token.userToken);
	}

	@Test
	@DisplayName("Authorization 헤더가 null이면 null 반환")
	void getToken_FromHeader_fail_nullHeader() {
		assertThat(jwtProvider.getTokenFromHeader(null)).isNull();
	}

	@Test
	@DisplayName("Authorization 헤더에 Bearer 접두어가 없으면 null 반환")
	void getToken_FromHeader_fail_invalidPrefix() {
		assertThat(jwtProvider.getTokenFromHeader("InvalidToken")).isNull();
	}
}
