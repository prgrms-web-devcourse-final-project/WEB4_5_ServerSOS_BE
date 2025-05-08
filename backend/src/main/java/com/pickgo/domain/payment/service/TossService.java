package com.pickgo.domain.payment.service;

import com.pickgo.domain.payment.config.TossPaymentConfig;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TossService {
    private final TossPaymentConfig tossPaymentConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public void confirmPayment(String paymentKey, String orderId, Integer amount) {
        try {
            HttpHeaders headers = buildHeaders();

            Map<String, Object> payload = new HashMap<>();
            payload.put("paymentKey", paymentKey);
            payload.put("orderId", orderId);
            payload.put("amount", amount);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(
                    TossPaymentConfig.apiUrl + "/confirm",
                    entity,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            throw new BusinessException(RsCode.PAYMENT_TOSS_FAILED);
        }
    }

    public void cancelPayment(String paymentKey) {
        try {
            HttpHeaders headers = buildHeaders();

            // TODO : 프론트에서 예외 메시지 지정 필요
            Map<String, Object> payload = Map.of(
                    "cancelReason", "사용자 요청으로 인한 취소"
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.postForEntity(
                    TossPaymentConfig.apiUrl + "/" + paymentKey + "/cancel",
                    entity,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            throw new BusinessException(RsCode.PAYMENT_TOSS_CANCEL_FAILED);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tossPaymentConfig.getAuthorizations());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
