package com.pickgo.admin.controller;

import static com.pickgo.global.response.RsCode.*;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pickgo.admin.service.AdminService;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.response.RsData;
import com.pickgo.member.dto.MemberSimpleResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin API", description = "Admin API 엔드포인트")
public class AdminController {
	private final AdminService adminService;

	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Member 페이징 조회")
	@GetMapping("/members")
	public RsData<PageResponse<MemberSimpleResponse>> getMembers(
		@ParameterObject @PageableDefault(sort = "id") Pageable pageable
	) {
		return RsData.from(SUCCESS, adminService.getPagedMembers(pageable));
	}
}
