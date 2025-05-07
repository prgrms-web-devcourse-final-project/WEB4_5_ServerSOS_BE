package com.pickgo.domain.reservation.controller;

import com.pickgo.domain.member.dto.MemberPrincipal;
import com.pickgo.domain.reservation.dto.request.ReservationCreateRequest;
import com.pickgo.domain.reservation.dto.response.ReservationDetailResponse;
import com.pickgo.domain.reservation.dto.response.ReservationSimpleResponse;
import com.pickgo.domain.reservation.service.ReservationService;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.pickgo.global.response.RsCode.RESERVATION_CANCEL;
import static com.pickgo.global.response.RsCode.SUCCESS;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "공연 예매 관련 API")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(
            summary = "예약 생성",
            description = "공연 세션 ID와 좌석 ID 목록을 기반으로 예약을 생성합니다."
    )
    public RsData<ReservationSimpleResponse> createReservation(
            @Parameter(hidden = true) @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody ReservationCreateRequest request
    ) {
        ReservationSimpleResponse response = reservationService.createReservation(principal.id(), request);
        return RsData.from(SUCCESS, response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "예약 상세 조회",
            description = "예약 ID를 기반으로 예약 상세 정보를 조회합니다."
    )
    public RsData<ReservationDetailResponse> getReservation(
            @Parameter(description = "예약 ID", example = "1") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal MemberPrincipal principal
    ) {
        ReservationDetailResponse response = reservationService.getReservation(id, principal.id());
        return RsData.from(SUCCESS, response);
    }

    @GetMapping("/me")
    @Operation(
            summary = "내 예약 목록 조회",
            description = "로그인한 사용자의 예약 목록을 페이지 단위로 조회합니다.(PAID,CANCELED 상태만 보여준다)"
    )
    public RsData<PageResponse<ReservationSimpleResponse>> getMyReservationList(
            @Parameter(hidden = true) @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ReservationSimpleResponse> response = reservationService.getMyReservationList(principal.id(), page, size);
        return RsData.from(SUCCESS, response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "예약 삭제",
            description = "예약 ID를 기반으로 예약을 삭제합니다. - 예약 생성 후 뒤로가기 시 발생"
    )
    public RsData<?> deleteReservation(
            @Parameter(description = "예약 ID", example = "1") @PathVariable Long id
    ) {
        reservationService.deleteReservation(id);
        return RsData.from(SUCCESS);
    }

    @PostMapping("/{id}/cancel")
    @Operation(
            summary = "예약 취소",
            description = "예약 ID를 기반으로 예약을 취소합니다. - 예약 완료 후 예약 내역에서 취소"
    )
    public RsData<?> cancelReservation(
            @Parameter(description = "예약 ID", example = "1") @PathVariable Long id
    ) {
        reservationService.cancelReservation(id);
        return RsData.from(RESERVATION_CANCEL);
    }
}