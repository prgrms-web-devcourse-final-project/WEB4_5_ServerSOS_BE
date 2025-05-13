package com.pickgo.domain.member.admin.controller;

import com.pickgo.domain.member.admin.service.AdminService;
import com.pickgo.domain.log.dto.MemberLogResponse;
import com.pickgo.domain.log.dto.PaymentLogResponse;
import com.pickgo.domain.log.dto.ReservationLogResponse;
import com.pickgo.domain.log.service.LogService;
import com.pickgo.domain.member.member.dto.MemberSimpleResponse;
import com.pickgo.global.response.PageResponse;
import com.pickgo.global.response.RsCode;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.pickgo.global.response.RsCode.SUCCESS;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin API", description = "Admin API 엔드포인트")
public class AdminController {
    private final AdminService adminService;
    private final LogService logService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Member 페이징 조회")
    @GetMapping("/members")
    public RsData<PageResponse<MemberSimpleResponse>> getMembers(
            @ParameterObject @PageableDefault(sort = "id") Pageable pageable
    ) {
        return RsData.from(SUCCESS, adminService.getPagedMembers(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member-histories")
    @Operation(summary = "회원 로그 조회", description = "관리자가 전체 회원 로그를 페이지 단위로 조회합니다.")
    public RsData<PageResponse<MemberLogResponse>> getMemberLogs(
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<MemberLogResponse> response = logService.getMemberLogs(page, size);
        return RsData.from(RsCode.SUCCESS, response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reservation-histories")
    @Operation(summary = "예약 로그 조회", description = "관리자가 전체 예약 로그를 페이지 단위로 조회합니다.")
    public RsData<PageResponse<ReservationLogResponse>> getReservationLog(
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ReservationLogResponse> response = logService.getReservationLogs(page, size);
        return RsData.from(RsCode.SUCCESS, response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/payment-histories")
    @Operation(summary = "결제 로그 조회", description = "관리자가 전체 결제 로그를 페이지 단위로 조회합니다.")
    public RsData<PageResponse<PaymentLogResponse>> getPaymentLogs(
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<PaymentLogResponse> response = logService.getPaymentLogs(page, size);
        return RsData.from(RsCode.SUCCESS, response);
    }
}
