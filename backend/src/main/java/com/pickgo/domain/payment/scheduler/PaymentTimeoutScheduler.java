package com.pickgo.domain.payment.scheduler;

import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;
import com.pickgo.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTimeoutScheduler {

    private final PaymentRepository paymentRepository;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void deleteStalePayments() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);

        List<Payment> stalePayments = paymentRepository.findByStatusAndCreatedAtBefore(
                PaymentStatus.PENDING, threshold
        );

        for (Payment payment :stalePayments) {
            payment.setStatus(PaymentStatus.EXPIRED);
        }
    }
}
