package com.pickgo.domain.member.member.service;

import com.pickgo.global.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    private static final Duration EXPIRE_DURATION = Duration.ofMinutes(5);

    public void sendCode(String email) {
        String code = generateCode();

        // redis 저장 ttl 5분
        redisTemplate.opsForValue().set(getRedisKey(email), code, EXPIRE_DURATION);

        // 이메일 전송
        emailService.sendEmail(email, "[pickgo] 이메일 인증 코드", "인증 코드: " + code);
    }

    public boolean verifyCode(String email, String code) {
        String key = getRedisKey(email);
        String savedCode = redisTemplate.opsForValue().get(key);

        if (code.equals(savedCode)) {
            redisTemplate.delete(key);
            redisTemplate.opsForValue().set(getVerifiedKey(email), "true", EXPIRE_DURATION);
            return true;
        }
        return false;
    }

    public boolean isVerified(String email) {
        String key = getVerifiedKey(email);
        String verified = redisTemplate.opsForValue().get(key);
        if ("true".equals(verified)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private String getRedisKey(String email) {
        return "email:verify:" + email;
    }

    private String getVerifiedKey(String email) {
        return "email:verify:success:" + email;
    }
}
