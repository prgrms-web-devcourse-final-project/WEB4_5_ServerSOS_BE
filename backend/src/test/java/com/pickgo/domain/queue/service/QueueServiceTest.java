package com.pickgo.domain.queue.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pickgo.domain.auth.token.service.TokenService;
import com.pickgo.domain.queue.dto.WaitingState;
import com.pickgo.domain.queue.repository.redis.RedisQueueRepository;
import com.pickgo.global.infra.server.ServerRegistry;
import com.pickgo.global.infra.stream.redis.RedisStreamPublisher;
import com.pickgo.global.init.ServerIdProvider;

@DataRedisTest
@Import({QueueService.class, RedisQueueRepository.class})
class QueueServiceTest {

    @Autowired
    private QueueService queueService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private RedisStreamPublisher redisStreamPublisher;

    @MockitoBean
    private ServerIdProvider serverIdProvider;

    @MockitoBean
    private ServerRegistry serverRegistry;

    private final Long performanceSessionId = 1L;
    private final String connectionId = "conn-1";
    private final String serverId = "server-1";
    private final String streamKey = "queue_stream:server_id:" + serverId;

    @BeforeEach
    void setUp() {
        when(serverIdProvider.getServerId()).thenReturn(serverId);
        when(serverRegistry.getServerId(connectionId)).thenReturn(serverId);
    }

    @AfterEach
    void tearDown() {
        queueService.clearAll();
    }

    @Test
    void 대기열에_입장하면_추가되고_상태가_발행된다() {
        queueService.enterWaitingLine(performanceSessionId, connectionId);

        List<String> line = queueService.getLine(performanceSessionId);
        assertThat(line).contains(connectionId);

        verify(redisStreamPublisher).publish(eq(streamKey), any(Map.class));
    }

    @Test
    void 대기열에서_제거하면_사라진다() {
        queueService.enterWaitingLine(performanceSessionId, connectionId);

        queueService.remove(performanceSessionId, connectionId);

        List<String> line = queueService.getLine(performanceSessionId);
        assertThat(line).doesNotContain(connectionId);
    }

    @Test
    void 대기열이_비었는지_확인할수있다() {
        assertThat(queueService.isEmpty(performanceSessionId)).isTrue();

        queueService.enterWaitingLine(performanceSessionId, connectionId);

        assertThat(queueService.isEmpty(performanceSessionId)).isFalse();
    }

    @Test
    void 대기열의_사이즈를_정확히반환한다() {
        queueService.enterWaitingLine(performanceSessionId, connectionId);

        int size = queueService.getSize(performanceSessionId);
        assertThat(size).isEqualTo(1);
    }

    @Test
    void 대기열의_순번을_정확히반환한다() {
        queueService.enterWaitingLine(performanceSessionId, connectionId);

        int position = queueService.getPosition(performanceSessionId, connectionId);
        assertThat(position).isEqualTo(1);
    }

    @Test
    void 대기열에서_상위_n개를_꺼낼수있다() {
        queueService.enterWaitingLine(performanceSessionId, connectionId);

        List<String> top = queueService.pollTopCount(performanceSessionId, 1);
        assertThat(top).containsExactly(connectionId);
    }

    @Test
    void 대기열에_있는지_확인할수있다() {
        assertThat(queueService.isIn(performanceSessionId, connectionId)).isFalse();

        queueService.enterWaitingLine(performanceSessionId, connectionId);

        assertThat(queueService.isIn(performanceSessionId, connectionId)).isTrue();
    }

    @Test
    void 모든세션ID를_정확히_반환한다() {
        queueService.enterWaitingLine(performanceSessionId, connectionId);

        List<Long> sessionIds = queueService.getAllPerformanceSessionIds();
        assertThat(sessionIds).contains(performanceSessionId);
    }

    @Test
    void 대기열상태를_직접_발행할수있다() {
        WaitingState state = WaitingState.of(1, 10, "1초");

        queueService.publishWaitingState(performanceSessionId, connectionId, state);

        verify(redisStreamPublisher).publish(eq(streamKey), any(Map.class));
    }
}
