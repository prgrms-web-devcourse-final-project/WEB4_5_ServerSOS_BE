package com.pickgo.domain.queue.stream;

import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pickgo.domain.queue.repository.QueueRepository;

@DataRedisTest
@Import({QueueStreamPublisher.class})
class QueueStreamPublisherTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private QueueStreamPublisher publisher;

    @MockitoBean
    private QueueRepository queueRepository;

    private final String serverId = "server-1";
    private final String connectionId = "conn-1";
    private final Long performanceSessionId = 1L;

    @BeforeEach
    void setUp() {
        when(queueRepository.getServerId(performanceSessionId, connectionId)).thenReturn(serverId);
    }

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys("queue_stream:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void 대기열_스트림_발행이_정상적으로_수행된다() {
        // given
        Map<String, String> data = Map.of("type", "TEST", "key", "value");

        // when
        publisher.publish(performanceSessionId, connectionId, data);

        // then
        var entries = redisTemplate.opsForStream().range("queue_stream:serverId:" + serverId, Range.unbounded());

        Assertions.assertThat(entries).isNotEmpty();
        Assertions.assertThat(entries.getFirst().getValue())
                .containsEntry("type", "TEST")
                .containsEntry("key", "value");
    }
}
