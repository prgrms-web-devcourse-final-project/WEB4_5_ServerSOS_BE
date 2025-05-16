package com.pickgo.domain.queue.stream;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pickgo.domain.queue.dto.EntryPermission;
import com.pickgo.domain.queue.dto.WaitingState;
import com.pickgo.global.infra.sse.SseHandler;
import com.pickgo.global.init.ServerIdProvider;

@DataRedisTest
@Import({QueueStreamConsumer.class})
class QueueStreamConsumerTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @MockitoBean
    private ServerIdProvider serverIdProvider;

    @MockitoBean
    private SseHandler sseHandler;

    @Autowired
    private QueueStreamConsumer queueStreamConsumer;

    private final String serverId = "server-1";
    private final String connectionId = "conn-1";
    private final String streamKey = "queue_stream:server_id:" + serverId;
    private final String entryToken = "entryToken";

    @BeforeEach
    void setUp() {
        when(serverIdProvider.getServerId()).thenReturn(serverId);
        queueStreamConsumer.initConsumerGroup();
    }

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys("queue_stream:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void 입장준비_메시지를_정상적으로_처리한다() throws IOException {
        // given
        redisTemplate.opsForStream().add(streamKey, Map.of(
                "type", "ready",
                "connection_id", connectionId,
                "entry_token", entryToken
        ));

        // when
        queueStreamConsumer.consume();

        // then
        verify(sseHandler).sendMessage(eq("ready"), eq(connectionId), argThat(arg -> {
            if (arg instanceof EntryPermission ep) {
                return ep.entryToken().equals(entryToken);
            }
            return false;
        }));
        verify(sseHandler).complete(connectionId);
    }

    @Test
    void 대기열상태_메시지를_정상적으로_처리한다() throws IOException {
        // given
        redisTemplate.opsForStream().add(streamKey, Map.of(
                "type", "wait",
                "connection_id", connectionId,
                "position", "1",
                "total_count", "10",
                "estimated_time", "1초"
        ));

        // when
        queueStreamConsumer.consume();

        // then
        verify(sseHandler).sendMessage(eq("wait"), eq(connectionId), eq(WaitingState.of(1, 10, "1초")));
    }

    @Test
    void 메시지가없으면_아무일도안한다() throws IOException {
        // when
        queueStreamConsumer.consume();

        // then
        verifyNoInteractions(sseHandler);
    }

    @Test
    void 잘못된타입의_메시지는_예외를_발생시키지않고_건너뛴다() throws IOException {
        // given
        redisTemplate.opsForStream().add(streamKey, Map.of(
                "type", "invalid",
                "connection_id", connectionId
        ));

        // when
        queueStreamConsumer.consume();

        // then
        verifyNoMoreInteractions(sseHandler);
    }
}
