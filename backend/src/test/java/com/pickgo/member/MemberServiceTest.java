package com.pickgo.member;

import static com.pickgo.domain.member.entity.enums.Authority.*;
import static com.pickgo.domain.member.entity.enums.SocialProvider.*;
import static com.pickgo.global.response.RsCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.pickgo.global.s3.S3Uploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.pickgo.domain.auth.service.TokenService;
import com.pickgo.domain.member.dto.LoginRequest;
import com.pickgo.domain.member.dto.LoginResponse;
import com.pickgo.domain.member.dto.MemberDetailResponse;
import com.pickgo.domain.member.dto.MemberPasswordUpdateRequest;
import com.pickgo.domain.member.dto.MemberSimpleResponse;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.member.service.MemberService;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.exception.BusinessException;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private S3Uploader s3Uploader;

    @InjectMocks
    private MemberService memberService;

    private final String email = "test@example.com";
    private final String password = "test_password";
    private final String encodedPassword = "encoded_password";
    private final String nickname = "test_user";
    private final String profile = "https://url.kr/estdgi";
    private final String accessToken = "access-token";

    private final UUID userId = UUID.randomUUID();

    private Member getMockMember() {
        Member member = Member.builder()
                .id(userId)
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .profile(profile)
                .authority(USER)
                .socialProvider(NONE)
                .build();

        // 수동으로 createdAt / modifiedAt 설정
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(member, "createdAt", now);
        ReflectionTestUtils.setField(member, "modifiedAt", now);
        return member;
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        LoginRequest request = new LoginRequest(email, password);
        Member member = getMockMember();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(tokenService.genAccessToken(member)).thenReturn(accessToken);

        LoginResponse result = memberService.login(request, response);

        assertThat(result.accessToken()).isEqualTo(accessToken);
        verify(tokenService).createRefreshToken(member, response);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 유저")
    void login_fail_notFound() {
        LoginRequest request = new LoginRequest(email, password);
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.login(request, response))
                .isInstanceOf(BusinessException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_invalidPassword() {
        LoginRequest request = new LoginRequest(email, password);
        Member member = getMockMember();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertThatThrownBy(() -> memberService.login(request, response))
                .isInstanceOf(BusinessException.class)
                .hasMessage(MEMBER_LOGIN_FAILED.getMessage());
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    void delete_success() {
        Member member = getMockMember();
        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));

        memberService.delete(userId, response);

        verify(memberRepository).delete(member);
        verify(tokenService).removeRefreshTokenCookie(response);
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 존재하지 않는 유저")
    void delete_fail_notFound() {
        when(memberRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.delete(userId, response))
                .isInstanceOf(BusinessException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getDetail_success() {
        Member member = getMockMember();
        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));

        MemberDetailResponse result = memberService.getDetail(userId);

        assertThat(result.email()).isEqualTo(member.getEmail());
        assertThat(result.nickname()).isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePassword_success() {
        Member member = getMockMember();
        String newRawPassword = "newPassword!";
        String encodedNewPassword = "encodedNew";

        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));
        when(passwordEncoder.encode(newRawPassword)).thenReturn(encodedNewPassword);

        memberService.updatePassword(userId, new MemberPasswordUpdateRequest(newRawPassword));

        assertThat(member.getPassword()).isEqualTo(encodedNewPassword);
    }

    @Test
    @DisplayName("유저 목록 조회 성공")
    void getPagedMembers_success() {
        Member member = getMockMember();
        PageRequest pageable = PageRequest.of(0, 10);

        when(memberRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(java.util.List.of(member)));

        PageResponse<MemberSimpleResponse> response = memberService.getPagedMembers(pageable);

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().getFirst().email()).isEqualTo(email);
    }

    @Test
    @DisplayName("프로필 이미지 업데이트 성공")
    void updateProfileImage_success() {
        Member member = getMockMember();
        MultipartFile mockFile = mock(MultipartFile.class);

        when(s3Uploader.upload(mockFile, "profile")).thenReturn("https://mock-s3.com/profile/newProfile.jpg");
        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));

        String updatedImageUrl = memberService.updateProfileImage(userId, mockFile);

        verify(s3Uploader).upload(mockFile, "profile");
        verify(s3Uploader).delete(profile);
        assertThat(updatedImageUrl).isEqualTo("https://mock-s3.com/profile/newProfile.jpg");
        assertThat(member.getProfile()).isEqualTo("https://mock-s3.com/profile/newProfile.jpg");
    }

    @Test
    @DisplayName("이미지 파일이 null일 경우 기본 이미지로 설정")
    void updateProfileImage_defaultImage() {
        ReflectionTestUtils.setField(memberService, "profile", "https://url.kr/estdgi");

        Member member = getMockMember();
        MultipartFile mockFile = null;

        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));

        String updatedImageUrl = memberService.updateProfileImage(userId, mockFile);

        verify(s3Uploader, never()).upload(any(), any());
        assertThat(updatedImageUrl).isEqualTo(profile);
        assertThat(member.getProfile()).isEqualTo(profile);
    }
}
