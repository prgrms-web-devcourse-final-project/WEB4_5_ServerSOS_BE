package com.pickgo.domain.payment.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@Getter
public class TossPaymentConfig {
    @Value("${custom.toss.client_key}")
    private String widgetClientKey;

    @Value("${custom.toss.secret_key}")
    private String widgetSecretKey;

    public String getAuthorizations() {
        String raw = widgetSecretKey + ":";
        String encoded = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    public static final String apiUrl = "https://api.tosspayments.com/v1/payments";
}
