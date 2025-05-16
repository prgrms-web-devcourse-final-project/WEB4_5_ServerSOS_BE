package com.pickgo.global.init;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;

/**
 * 서버 식별 ID Provider
 */
@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class ServerIdProvider {

    private String serverId;

    /**
     * 서버 ID 초기화
     */
    @PostConstruct
    public void init() {
        try {
            // AWS EC2 인스턴스 ID를 이용하여 서버 ID 초기화
            serverId = EC2MetadataUtils.getInstanceId();
        } catch (Exception e) {
            log.error("Failed to get server ID, using random ID instead: ", e);

            // 랜덤 값으로 서버 ID 초기화
            serverId = UUID.randomUUID().toString();
        }
    }
}
