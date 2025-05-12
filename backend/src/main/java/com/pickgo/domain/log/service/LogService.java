package com.pickgo.domain.log.service;

import com.pickgo.domain.log.dto.MemberLogResponse;
import com.pickgo.domain.log.dto.PaymentLogResponse;
import com.pickgo.domain.log.dto.ReservationLogResponse;
import com.pickgo.domain.log.entity.MemberHistory;
import com.pickgo.domain.log.entity.PaymentHistory;
import com.pickgo.domain.log.entity.ReservationHistory;
import com.pickgo.domain.log.repository.MemberHistoryRepository;
import com.pickgo.domain.log.repository.PaymentHistoryRepository;
import com.pickgo.domain.log.repository.ReservationHistoryRepository;
import com.pickgo.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
    private final MemberHistoryRepository memberHistoryRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public PageResponse<MemberLogResponse> getMemberLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<MemberHistory> histories = memberHistoryRepository.findAll(pageable);

        return PageResponse.from(histories, MemberLogResponse::from);
    }

    public PageResponse<ReservationLogResponse> getReservationLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ReservationHistory> histories = reservationHistoryRepository.findAll(pageable);

        return PageResponse.from(histories, ReservationLogResponse::from);
    }

    public PageResponse<PaymentLogResponse> getPaymentLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PaymentHistory> histories = paymentHistoryRepository.findAll(pageable);

        return PageResponse.from(histories, PaymentLogResponse::from);
    }
}
