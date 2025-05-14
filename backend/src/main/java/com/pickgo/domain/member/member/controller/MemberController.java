package com.pickgo.domain.member.member.controller;

import com.pickgo.domain.member.member.dto.*;
import com.pickgo.domain.member.member.service.EmailAuthService;
import com.pickgo.domain.member.member.service.MemberService;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.pickgo.global.response.RsCode.EMAIL_VERIFICATION_FAILED;
import static com.pickgo.global.response.RsCode.SUCCESS;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member API", description = "Member API 엔드포인트")
public class MemberController {

    private final MemberService memberService;
    private final EmailAuthService emailAuthService;

    @Operation(summary = "회원가입")
    @PostMapping
    public RsData<MemberDetailResponse> signup(@RequestBody MemberCreateRequest request) {
        return RsData.from(SUCCESS, memberService.save(request));
    }

    @Operation(summary = "인증 이메일 요청")
    @PostMapping("/email/send-code")
    public RsData<?> sendCode(@RequestParam String email) {
        emailAuthService.sendCode(email);
        return RsData.from(SUCCESS);
    }

    @Operation(summary = "이메일 인증 코드 검증")
    @PostMapping("/email/verify-code")
    public RsData<?> verifyEmailCode(@RequestParam String email, @RequestParam String code) {
        boolean verified = emailAuthService.verifyCode(email, code);
        return verified
                ? RsData.from(SUCCESS)
                : RsData.from(EMAIL_VERIFICATION_FAILED);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public RsData<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        return RsData.from(SUCCESS, memberService.login(request, response));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public RsData<?> logout(HttpServletResponse response) {
        memberService.logout(response);
        return RsData.from(SUCCESS);
    }

    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/me")
    public RsData<?> signout(
            @AuthenticationPrincipal MemberPrincipal principal,
            HttpServletResponse response
    ) {
        memberService.delete(principal.id(), response);
        return RsData.from(SUCCESS);
    }

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public RsData<MemberDetailResponse> myInfo(@AuthenticationPrincipal MemberPrincipal principal) {
        return RsData.from(SUCCESS, memberService.getDetail(principal.id()));
    }

    @Operation(summary = "내 정보 수정")
    @PutMapping("/me")
    public RsData<MemberDetailResponse> updateMyInfo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody MemberUpdateRequest request
    ) {
        return RsData.from(SUCCESS, memberService.updateMyInfo(principal.id(), request));
    }

    @Operation(summary = "비밀번호 변경")
    @PutMapping("/me/password")
    public RsData<?> updatePassword(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody MemberPasswordUpdateRequest request
    ) {
        memberService.updatePassword(principal.id(), request);
        return RsData.from(SUCCESS);
    }

    @Operation(summary = "프로필 이미지 수정")
    @PutMapping("/me/profile")
    public RsData<String> updateProfileImage(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam MultipartFile image
    ) {
        String imageUrl = memberService.updateProfileImage(principal.id(), image);
        return RsData.from(SUCCESS, imageUrl);
    }
}
