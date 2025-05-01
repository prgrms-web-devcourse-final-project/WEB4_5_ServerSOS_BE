package com.pickgo.domain.reservation.controller;

import com.pickgo.domain.member.dto.MemberPrincipal;
import com.pickgo.domain.reservation.dto.request.ReservationCreateRequest;
import com.pickgo.domain.reservation.dto.response.ReservationDetailResponse;
import com.pickgo.domain.reservation.dto.response.ReservationSimpleResponse;
import com.pickgo.domain.reservation.service.ReservationService;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.pickgo.global.response.RsCode.SUCCESS;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "예약 관련 API")
public class ReservationController {

    private final ReservationService reservationService;


    @PostMapping
    @Operation(summary = "예약 생성", description = "공연 세션 ID와 좌석 ID 목록을 기반으로 예약을 생성합니다.")
    public RsData<ReservationSimpleResponse> createReservation(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody ReservationCreateRequest request
    ) {
        ReservationSimpleResponse response = reservationService.createReservation(principal.id(), request);
        return RsData.from(SUCCESS, response);
    }

    @GetMapping("/{id}")
    public RsData<ReservationDetailResponse> getReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        ReservationDetailResponse response = reservationService.getReservation(id, principal.id());
        return RsData.from(SUCCESS, response);
    }

    @GetMapping("/me")
    public RsData<PageResponse<ReservationSimpleResponse>> getMyReservationList(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ReservationSimpleResponse> response = reservationService.getMyReservationList(principal.id(),page,size);
        return RsData.from(SUCCESS, response);
    }

}
