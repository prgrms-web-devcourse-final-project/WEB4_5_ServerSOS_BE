package com.pickgo.domain.queue.stream;

import static com.pickgo.domain.queue.stream.QueueStreamConsumer.*;
import static org.awaitility.Awaitility.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pickgo.domain.auth.token.service.TokenService;
import com.pickgo.domain.queue.dto.EntryPermission;
import com.pickgo.domain.queue.dto.QueueSession;
import com.pickgo.domain.queue.dto.WaitingState;
import com.pickgo.global.config.thread.ExecutorConfig;
import com.pickgo.global.infra.sse.SseHandler;
import com.pickgo.global.init.ServerIdProvider;

@DataRedisTest
@Import({QueueStreamConsumer.class, ExecutorConfig.class, ServerIdProvider.class})
class QueueStreamConsumerTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @MockitoBean
    private SseHandler sseHandler;

    @MockitoBean
    private TokenService tokenService;

    @Autowired
    private QueueStreamConsumer queueStreamConsumer;

    private final String connectionId = "conn-1";
    private final String entryToken = "entryToken";
    private String streamKey;
    private String consumerGroup;
    private String consumerName;

    @BeforeEach
    void setUp() {
        streamKey = queueStreamConsumer.getStreamKey();
        consumerGroup = queueStreamConsumer.getConsumerGroupName();
        consumerName = queueStreamConsumer.getConsumerName();

        queueStreamConsumer.initConsumerGroup();
    }

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys(QUEUE_STREAM_PREFIX + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void 입장준비_메시지를_정상적으로_처리한다() {
        // given
        redisTemplate.opsForStream().add(streamKey, Map.of(
                "type", "ready",
                "connection_id", connectionId,
                "entry_token", entryToken
        ));
        QueueSession session = QueueSession.builder().connectionId(connectionId).build();

        when(tokenService.genEntryToken(anyLong(), any())).thenReturn(entryToken);
        when(sseHandler.getSession(connectionId, QueueSession.class)).thenReturn(session);

        // when
        queueStreamConsumer.consume(consumerGroup, consumerName, streamKey);

        // then (비동기 고려해서 await으로 검증)
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() ->
                verify(sseHandler).sendMessage(eq("ready"), eq(connectionId), any(EntryPermission.class))
        );
    }

    @Test
    void 대기열상태_메시지를_정상적으로_처리한다() {
        // given
        redisTemplate.opsForStream().add(streamKey, Map.of(
                "type", "wait",
                "connection_id", connectionId,
                "position", "1",
                "total_count", "10",
                "estimated_time", "1초"
        ));

        // when
        queueStreamConsumer.consume(consumerGroup, consumerName, streamKey);

        // then (비동기 고려해서 await으로 검증)
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() ->
                verify(sseHandler).sendMessage(eq("wait"), eq(connectionId), any(WaitingState.class))
        );
    }

    @Test
    void 메시지가없으면_아무일도안한다() {
        // when
        queueStreamConsumer.consume(consumerGroup, consumerName, streamKey);

        // then
        verifyNoInteractions(sseHandler);
    }

    @Test
    void 잘못된타입의_메시지는_예외를_발생시키지않고_건너뛴다() {
        // given
        redisTemplate.opsForStream().add(streamKey, Map.of(
                "type", "invalid",
                "connection_id", connectionId
        ));

        // when
        queueStreamConsumer.consume(consumerGroup, consumerName, streamKey);

        // then
        verifyNoMoreInteractions(sseHandler);
    }
}
