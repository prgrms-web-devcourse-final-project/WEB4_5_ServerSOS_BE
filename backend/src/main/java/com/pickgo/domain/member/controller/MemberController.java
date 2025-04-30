package com.pickgo.domain.member.controller;

import static com.pickgo.global.response.RsCode.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pickgo.domain.member.dto.LoginRequest;
import com.pickgo.domain.member.dto.LoginResponse;
import com.pickgo.domain.member.dto.MemberCreateRequest;
import com.pickgo.domain.member.dto.MemberDetailResponse;
import com.pickgo.domain.member.dto.MemberPasswordUpdateRequest;
import com.pickgo.domain.member.dto.MemberPrincipal;
import com.pickgo.domain.member.dto.MemberUpdateRequest;
import com.pickgo.domain.member.service.MemberService;
import com.pickgo.global.response.RsData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member API", description = "Member API 엔드포인트")
public class MemberController {

	private final MemberService memberService;

	@Operation(summary = "회원가입")
	@PostMapping
	public RsData<MemberDetailResponse> signup(@RequestBody MemberCreateRequest request) {
		return RsData.from(SUCCESS, memberService.save(request));
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
}
