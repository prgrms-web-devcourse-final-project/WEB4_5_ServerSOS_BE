package com.pickgo.domain.member.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.log.entity.MemberHistory;
import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.enums.ActorType;
import com.pickgo.domain.log.repository.MemberHistoryRepository;
import com.pickgo.domain.member.member.dto.MemberCreateRequest;
import com.pickgo.domain.member.member.dto.MemberPasswordUpdateRequest;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.member.member.entity.enums.SocialProvider;
import com.pickgo.domain.member.member.repository.MemberRepository;
import com.pickgo.global.jwt.JwtProvider;
import com.pickgo.global.logging.service.HistorySaveService;
import com.pickgo.global.token.TestToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.pickgo.domain.member.member.entity.enums.Authority.USER;
import static com.pickgo.global.response.RsCode.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

    @Autowired
    private HistorySaveService historySaveService;

    @Autowired
    private MemberHistoryRepository memberHistoryRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
                .profile("profile.jpg")
                .authority(USER)
                .socialProvider(SocialProvider.NONE)
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

        redisTemplate.opsForValue().set("email:verify:success:" + NEW_EMAIL, "true");

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.email").value(NEW_EMAIL));

//		MemberHistory history = memberHistoryRepository.findAll().get(0);
//		assertThat(history.getEmail()).isEqualTo(NEW_EMAIL);
//		assertThat(history.getAction()).isEqualTo(ActionType.MEMBER_SIGNUP);
//		assertThat(history.getActorType()).isEqualTo(ActorType.GUEST);
	}

    @Test
    @DisplayName("로그인 성공")
    void login_성공() throws Exception {
        redisTemplate.opsForValue().set("email:verify:success:" + testEmail, "true");

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

        MemberHistory history = memberHistoryRepository.findAll().get(0);
        assertThat(history.getAction()).isEqualTo(ActionType.MEMBER_SIGNUP);
        assertThat(history.getActorType()).isEqualTo(ActorType.GUEST);
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

    @Test
    @DisplayName("프로필 이미지 수정 성공")
    void updateProfileImage_성공() throws Exception {
        MockMultipartFile profileImage = new MockMultipartFile(
                "image",
                "newProfile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );

        mockMvc.perform(multipart("/api/members/me/profile")
                        .file(profileImage)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header("Authorization", "Bearer " + token.userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value("https://mock-s3.com/profile/newProfile.jpg"));
    }
}
