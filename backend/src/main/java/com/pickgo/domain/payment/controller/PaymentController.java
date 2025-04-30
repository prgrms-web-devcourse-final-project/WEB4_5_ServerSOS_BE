package com.pickgo.domain.payment.controller;

import com.pickgo.domain.auth.dto.MemberPrincipal;
import com.pickgo.domain.payment.dto.PaymentConfirmRequest;
import com.pickgo.domain.payment.dto.PaymentCreateRequest;
import com.pickgo.domain.payment.dto.PaymentDetailResponse;
import com.pickgo.domain.payment.dto.PaymentSimpleResponse;
import com.pickgo.domain.payment.service.PaymentService;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.exception.BusinessException;
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

    @Operation(summary = "결제 생성")
    @PostMapping
    public RsData<PaymentDetailResponse> createPayment(@RequestBody @Valid PaymentCreateRequest request) {
        try{
            PaymentDetailResponse response = paymentService.createPayment(request);
            return RsData.from(RsCode.CREATED, response);
        } catch (Exception e) {
            return RsData.from(RsCode.BAD_REQUEST, null);
        }
    }

    @Operation(summary = "내 결제 목록 조회")
    @GetMapping("/me")
    public RsData<PageResponse<PaymentSimpleResponse>> getMyPayments(
            @ParameterObject @PageableDefault(sort = "id") Pageable pageable,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        try{
            PageResponse<PaymentSimpleResponse> response = paymentService.getMyPayments(principal.id(), pageable);
            return RsData.from(RsCode.SUCCESS, response);
        } catch (Exception e) {
            return RsData.from(RsCode.NOT_FOUND, null);
        }
    }

    @Operation(summary = "결제 상세 조회")
    @GetMapping("/{id}")
    public RsData<PaymentDetailResponse> getPaymentDetail(@PathVariable Long id) {
        try{
            PaymentDetailResponse response = paymentService.getPaymentDetail(id);
            return RsData.from(RsCode.SUCCESS, response);
        } catch (Exception e) {
            return RsData.from(RsCode.NOT_FOUND, null);
        }
    }

    @Operation(summary = "결제 취소")
    @DeleteMapping("/{id}")
    public RsData<PaymentDetailResponse> cancelPayment(@PathVariable Long id) {
        try{
            PaymentDetailResponse response = paymentService.cancelPayment(id);
            return RsData.from(RsCode.SUCCESS, response);
        } catch (Exception e) {
            return RsData.from(RsCode.NOT_FOUND, null);
        }
    }

    @Operation(summary = "결제 승인")
    @PostMapping("/{id}/confirm")
    public RsData<PaymentDetailResponse> confirmPayment(
            @PathVariable Long id,
            @RequestBody @Valid PaymentConfirmRequest request
    ) {
        try {
            PaymentDetailResponse response = paymentService.confirmPayment(id, request);
            return RsData.from(RsCode.SUCCESS, response);
        } catch (BusinessException e) {
            return RsData.from(RsCode.BAD_REQUEST, null);
        }
    }

}
