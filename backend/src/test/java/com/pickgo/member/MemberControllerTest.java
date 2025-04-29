package com.pickgo.member;

import static com.pickgo.global.response.RsCode.*;
import static com.pickgo.member.entity.enums.Authority.*;
import static com.pickgo.member.entity.enums.SocialProvider.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.admin.TestToken;
import com.pickgo.global.jwt.JwtProvider;
import com.pickgo.member.dto.MemberCreateRequest;
import com.pickgo.member.dto.MemberPasswordUpdateRequest;
import com.pickgo.member.entity.Member;
import com.pickgo.member.repository.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TestToken token;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// 회원가입된 기존 유저
	private final String testEmail = "test@example.com";
	private final String testPassword = "test_password";
	private final String testNickname = "test_user";

	private Member getTestMember() {
		return Member.builder()
			.id(jwtProvider.getUserId(token.userToken))
			.email(testEmail)
			.password(passwordEncoder.encode(testPassword))
			.nickname(testNickname)
			.authority(USER)
			.socialProvider(NONE)
			.build();
	}

	@BeforeEach
	void setUp() {
		Member member = getTestMember();
		memberRepository.save(member);
	}

	@AfterEach
	void tearDown() {
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("회원가입 성공")
	void signup_성공() throws Exception {
		final String NEW_EMAIL = "new@example.com";

		MemberCreateRequest request = new MemberCreateRequest(
			NEW_EMAIL,
			testPassword,
			testNickname
		);

		mockMvc.perform(post("/api/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
			.andExpect(jsonPath("$.data.email").value(NEW_EMAIL));
	}

	@Test
	@DisplayName("로그인 성공")
	void login_성공() throws Exception {
		MemberCreateRequest request = new MemberCreateRequest(
			testEmail,
			testPassword,
			testNickname
		);
		mockMvc.perform(post("/api/members")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		String loginPayload = """
			{
				"email": "%s",
				"password": "%s"
			}
			""".formatted(testEmail, testPassword);

		mockMvc.perform(post("/api/members/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginPayload))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
			.andExpect(jsonPath("$.data.accessToken").exists());
	}

	@Test
	@DisplayName("내 정보 조회 - 인증 성공")
	void myInfo_성공() throws Exception {
		mockMvc.perform(get("/api/members/me")
				.header("Authorization", "Bearer " + token.userToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
			.andExpect(jsonPath("$.data.email").exists());
	}

	@Test
	@DisplayName("내 정보 조회 - 인증 실패")
	void myInfo_인증없음_실패() throws Exception {
		mockMvc.perform(get("/api/members/me")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("비밀번호 변경 성공")
	void updatePassword_성공() throws Exception {
		MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("newPassword123");

		mockMvc.perform(put("/api/members/me/password")
				.header("Authorization", "Bearer " + token.userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(SUCCESS.getCode()));
	}
}
