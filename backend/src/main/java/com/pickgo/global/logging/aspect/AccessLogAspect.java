package com.pickgo.global.logging.aspect;

import com.pickgo.global.logging.util.LogWriter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AccessLogAspect {

    private final LogWriter logWriter;

    @Around("execution(* com.pickgo..controller..*.*(..))")
    public Object logAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } finally {
            logWriter.writeAccessLog();
        }
    }
}
