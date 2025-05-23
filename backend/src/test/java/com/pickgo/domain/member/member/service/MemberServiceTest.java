package com.pickgo.domain.member.member.service;

import com.pickgo.domain.auth.token.service.TokenService;
import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.enums.ActorType;
import com.pickgo.domain.member.member.dto.*;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.member.member.entity.enums.SocialProvider;
import com.pickgo.domain.member.member.repository.MemberRepository;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.logging.dto.LogContext;
import com.pickgo.global.logging.util.LogContextUtil;
import com.pickgo.global.logging.util.LogWriter;
import com.pickgo.global.response.PageResponse;
import com.pickgo.global.s3.S3Uploader;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.pickgo.domain.member.member.entity.enums.Authority.USER;
import static com.pickgo.global.response.RsCode.MEMBER_LOGIN_FAILED;
import static com.pickgo.global.response.RsCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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

	@Mock
	private LogWriter logWriter;

	@Mock
	private LogContextUtil logContextUtil;

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
			.socialProvider(SocialProvider.NONE)
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
		LogContext mockLogContext = new LogContext("testUrl", "testAction", "testId", ActorType.SYSTEM); // 가짜 로그 컨텍스트 객체

		when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
		when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
		when(tokenService.genAccessToken(member)).thenReturn(accessToken);
		when(logContextUtil.extract()).thenReturn(mockLogContext);

		LoginResponse result = memberService.login(request, response);

		assertThat(result.accessToken()).isEqualTo(accessToken);
		verify(tokenService).createRefreshToken(member, response);
		verify(logWriter).writeMemberLog(member, ActionType.MEMBER_LOGIN, mockLogContext);
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
		LogContext mockLogContext = new LogContext("testUrl", "testAction", "testId", ActorType.SYSTEM); // 가짜 로그 컨텍스트 객체

		when(memberRepository.findById(userId)).thenReturn(Optional.of(member));
		when(logContextUtil.extract()).thenReturn(mockLogContext);

		memberService.delete(userId, response);

		verify(memberRepository).delete(member);
		verify(tokenService).removeRefreshTokenCookie(response);
		verify(logWriter).writeMemberLog(member, ActionType.MEMBER_DELETED, mockLogContext);
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
		assertThat(updatedImageUrl).isEqualTo("https://mock-s3.com/profile/newProfile.jpg");
		assertThat(member.getProfile()).isEqualTo("https://mock-s3.com/profile/newProfile.jpg");
	}

	@Test
	@DisplayName("이미지 파일이 비어있을 경우 기본 이미지로 설정")
	void updateProfileImage_defaultImage() {
		ReflectionTestUtils.setField(memberService, "profile", "https://url.kr/estdgi");

		Member member = getMockMember();
		MultipartFile mockFile = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0]);

		when(memberRepository.findById(userId)).thenReturn(Optional.of(member));

		String updatedImageUrl = memberService.updateProfileImage(userId, mockFile);

		verify(s3Uploader, never()).upload(any(), any());
		assertThat(updatedImageUrl).isEqualTo(profile);
		assertThat(member.getProfile()).isEqualTo(profile);
	}
}
