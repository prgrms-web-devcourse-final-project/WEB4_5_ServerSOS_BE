package com.pickgo.domain.queue.controller;

import static com.pickgo.global.response.RsCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.auth.token.service.TokenService;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.queue.service.EntryService;
import com.pickgo.domain.queue.service.WaitingService;
import com.pickgo.domain.queue.sse.SseEmitterHandler;
import com.pickgo.domain.queue.task.EntryPermissionTask;
import com.pickgo.global.token.TestToken;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WaitingControllerTest {

    private final int THREAD_COUNT = 5;
    private final List<String> accessTokens = new ArrayList<>();
    private final List<UUID> userIds = new ArrayList<>();
    @Autowired
    MockMvc mockMvc;
    @Autowired
    WaitingService waitingService;
    @Autowired
    EntryService entryService;
    @Autowired
    SseEmitterHandler sseEmitterHandler;
    @Autowired
    EntryPermissionTask entryPermissionTask;
    @Autowired
    TokenService tokenService;
    @Autowired
    TestToken token;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            UUID userId = UUID.randomUUID();
            String token = tokenService.genAccessToken(
                Member.builder().id(userId).authority(USER).build()
            );
            userIds.add(userId);
            accessTokens.add(token);
        }
    }

    @AfterEach
    void tearDown() {
        waitingService.clear();
        entryService.clear();
    }

    @Test
    @DisplayName("대기열 입장 및 SSE 구독 성공")
    void subscribeWaitingStatus_success() throws Exception {
        waitingService.enterWaitingLine(userIds.getFirst());
        mockMvc.perform(get("/api/queue/stream")
                .header("Authorization", "Bearer " + accessTokens.getFirst()))
            .andExpect(status().isOk());
    }

    @DisplayName("동시에 여러명 입장 요청 시 한명만 입장 처리")
    @Test
    void simultaneous_requests() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT); // 스레드 풀 생성
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT); // 멀티스레드 전용 카운트다운

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    mockMvc.perform(get("/api/queue/stream")
                            .header("Authorization", "Bearer " + accessTokens.get(idx)))
                        .andExpect(status().isOk());

                    sseEmitterHandler.subscribe(userIds.get(idx));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 메인스레드가 위에서 만든 스레드 완료될 때까지 대기

        assertThat(waitingService.getLine()).hasSize(THREAD_COUNT);

        // 입장 처리
        entryPermissionTask.enter();

        assertThat(entryService.getAll()).hasSize(1);
        assertThat(waitingService.getLine()).hasSize(THREAD_COUNT - 1);
    }

    @Test
    @DisplayName("결제 후 퇴장 성공")
    void exit_success() throws Exception {
        entryService.add(userIds.getFirst());

        mockMvc.perform(post("/api/queue/exit")
                .header("Authorization", "Bearer " + accessTokens.getFirst()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(SUCCESS.getCode()));
    }
}
