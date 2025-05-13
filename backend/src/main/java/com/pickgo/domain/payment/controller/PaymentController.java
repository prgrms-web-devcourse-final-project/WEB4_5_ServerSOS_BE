package com.pickgo.domain.payment.controller;

import com.pickgo.domain.member.member.dto.MemberPrincipal;
import com.pickgo.domain.payment.dto.PaymentConfirmRequest;
import com.pickgo.domain.payment.dto.PaymentCreateRequest;
import com.pickgo.domain.payment.dto.PaymentDetailResponse;
import com.pickgo.domain.payment.dto.PaymentSimpleResponse;
import com.pickgo.domain.payment.service.PaymentService;
import com.pickgo.global.response.PageResponse;
import com.pickgo.global.response.RsCode;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "Payment API 엔드포인트")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "결제 생성",
            description = "결제를 생성합니다. 결제 생성 후 결제 상세 정보를 반환합니다."
    )
    @PostMapping
    public RsData<PaymentDetailResponse> createPayment(
            @RequestBody @Valid PaymentCreateRequest request,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        PaymentDetailResponse response = paymentService.createPayment(request);
        return RsData.from(RsCode.CREATED, response);
    }

    @Operation(
            summary = "내 결제 목록 조회",
            description = "내 결제 목록을 조회합니다. 페이지네이션을 지원합니다."
    )
    @GetMapping("/me")
    public RsData<PageResponse<PaymentSimpleResponse>> getMyPayments(
            @ParameterObject @PageableDefault(sort = "id") Pageable pageable,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        PageResponse<PaymentSimpleResponse> response = paymentService.getMyPayments(principal.id(), pageable);
        return RsData.from(RsCode.SUCCESS, response);
    }

    @Operation(
            summary = "결제 상세 조회",
            description = "결제 상세 정보를 조회합니다. 결제 ID를 통해 조회합니다."
    )
    @GetMapping("/{id}")
    public RsData<PaymentDetailResponse> getPaymentDetail(@PathVariable Long id) {
        PaymentDetailResponse response = paymentService.getPaymentDetail(id);
        return RsData.from(RsCode.SUCCESS, response);
    }

//    @Operation(
//            summary = "결제 취소",
//            description = "결제를 취소합니다. 결제 ID를 통해 paymentKey를 조회 후 토스페이먼츠 API에 결제를 취소 요청을 보냅니다."
//    )
//    @PostMapping("/{id}/cancel")
//    public RsData<PaymentDetailResponse> cancelPayment(@PathVariable Long id) {
//        PaymentDetailResponse response = paymentService.cancelPayment(id);
//        return RsData.from(RsCode.SUCCESS, response);
//    }

    @Operation(
            summary = "결제 승인",
            description = "결제를 승인합니다. 토스페이먼츠 API에 결제 승인 요청을 보냅니다."
    )
    @PostMapping("/confirm")
    public RsData<PaymentDetailResponse> confirmPayment(
            @RequestBody @Valid PaymentConfirmRequest request
    ) {
        PaymentDetailResponse response = paymentService.confirmPayment(request);
        return RsData.from(RsCode.SUCCESS, response);
    }
}
