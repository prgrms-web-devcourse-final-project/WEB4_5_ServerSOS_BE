package com.pickgo.domain.reservation.controller;

import com.pickgo.domain.member.dto.MemberPrincipal;
import com.pickgo.domain.reservation.dto.request.ReservationCreateRequest;
import com.pickgo.domain.reservation.dto.response.ReservationSimpleResponse;
import com.pickgo.domain.reservation.service.ReservationService;
import com.pickgo.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.pickgo.global.response.RsCode.SUCCESS;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;


    @PostMapping
    public RsData<ReservationSimpleResponse> createReservation(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody ReservationCreateRequest request
    ) {
        ReservationSimpleResponse response = reservationService.createReservation(principal.id(), request);

        return RsData.from(SUCCESS, response);
    }
}
