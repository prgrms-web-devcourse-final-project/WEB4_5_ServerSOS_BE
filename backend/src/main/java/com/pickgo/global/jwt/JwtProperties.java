package com.pickgo.global.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
@ConfigurationProperties("custom.jwt") // application.yml에서 토큰 속성을 가져옴
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
