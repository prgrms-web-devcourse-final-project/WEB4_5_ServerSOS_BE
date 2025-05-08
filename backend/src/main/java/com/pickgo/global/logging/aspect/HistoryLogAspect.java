package com.pickgo.global.logging.aspect;

import com.pickgo.domain.log.repository.MemberHistoryRepository;
import com.pickgo.domain.log.repository.PaymentHistoryRepository;
import com.pickgo.domain.log.repository.ReservationHistoryRepository;
import com.pickgo.global.logging.annotation.LogMember;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class HistoryLogAspect {

    private final MemberHistoryRepository memberHistoryRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;

    @Around("@annotation(logMember)")
    public Object logMember(ProceedingJoinPoint joinPoint, LogMember logMember) throws Throwable {

        // 1. 요청 정보 추출
        // requestUri, method, actorId
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("==== 요청 정보 확인 ====");
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Method: " + request.getMethod());
        System.out.println("Authorization: " + request.getHeader("Authorization"));

        if (auth != null) {
            System.out.println("Principal: " + auth.getName());
            System.out.println("Is Authenticated: " + auth.isAuthenticated());
            System.out.println("Auth Class: " + auth.getClass().getSimpleName());
        } else {
            System.out.println("Authentication is null");
        }
        System.out.println("=======================");


        // 2. 실제 메서드 실행
        Object result = joinPoint.proceed();

        // 3. 로그 저장

        return result;
    }

}
