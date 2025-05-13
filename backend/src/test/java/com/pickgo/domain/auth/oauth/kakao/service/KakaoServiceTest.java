package com.pickgo.domain.auth.oauth.kakao.service;

import com.pickgo.domain.auth.token.service.TokenService;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.member.member.service.MemberService;
import com.pickgo.domain.auth.oauth.kakao.dto.KakaoToken;
import com.pickgo.domain.auth.oauth.kakao.dto.KakaoUserInfo;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.logging.util.LogWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

import static com.pickgo.domain.member.member.entity.enums.SocialProvider.KAKAO;
import static com.pickgo.global.response.RsCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KakaoServiceTest {

	@Mock
	private RestTemplate restTemplate;
	@Mock
	private TokenService tokenService;
	@Mock
	private MemberService memberService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private LogWriter logWriter;

	@InjectMocks
	private KakaoService kakaoService;

	private final String email = "kakao@example.com";
	private final String frontendUrl = "http://localhost:3000";

	private final UUID userId = UUID.randomUUID();
	private final KakaoUserInfo.KakaoAccount.Profile kakaoProfile = new KakaoUserInfo.KakaoAccount.Profile("닉네임",
		"http://profile.img");
	private final KakaoUserInfo.KakaoAccount kakaoAccount = new KakaoUserInfo.KakaoAccount(email, kakaoProfile);
	private final KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(1L, kakaoAccount);

	private Member getMockMember() {
		return Member.builder()
			.id(userId)
			.email(email)
			.password("")
			.nickname(kakaoProfile.nickname())
			.socialProvider(KAKAO)
			.build();
	}

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(kakaoService, "tokenUri", "https://kauth.kakao.com/oauth/token");
		ReflectionTestUtils.setField(kakaoService, "apiKey", "test-api-key");
		ReflectionTestUtils.setField(kakaoService, "redirectUri",
			"http://localhost:8080/api/oauth/kakao/login/redirect");
		ReflectionTestUtils.setField(kakaoService, "userInfoUri", "https://kapi.kakao.com/v2/user/me");
		ReflectionTestUtils.setField(kakaoService, "authorizeUri", "https://kauth.kakao.com/oauth/authorize");
		ReflectionTestUtils.setField(kakaoService, "profile", "https://default.profile.img");
	}

	@Test
	@DisplayName("카카오 로그인 성공 시 리다이렉트 반환 및 토큰 발급")
	void kakaoLogin_success() {
		// given
		String code = "auth-code";
		String accessToken = "access-token";
		Member member = getMockMember();
		KakaoToken kakaoToken = new KakaoToken(accessToken, "bearer", "refresh", 3600L, null, 86400L);

		when(restTemplate.postForEntity(anyString(), any(), eq(KakaoToken.class)))
			.thenReturn(ResponseEntity.ok(kakaoToken)); // kakao token 요청 응답

		when(restTemplate.postForEntity(anyString(), any(), eq(KakaoUserInfo.class)))
			.thenReturn(ResponseEntity.ok(kakaoUserInfo)); // kakao user info 응답

		when(memberService.getEntity(email)).thenThrow(new BusinessException(MEMBER_NOT_FOUND));
		when(memberService.saveEntity(any(Member.class))).thenReturn(member); // 기존 회원 없음 → 신규 저장

		when(request.getHeader("Origin")).thenReturn(frontendUrl); // 요청 헤더에 origin 정보 존재

		// when
		RedirectView redirectView = kakaoService.login(code, request, response);

		// then
		assertThat(redirectView.getUrl()).isEqualTo(frontendUrl);
		verify(tokenService).createRefreshToken(eq(member), eq(response));
	}
}
