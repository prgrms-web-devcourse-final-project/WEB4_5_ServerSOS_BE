package com.pickgo.global.infra.stream.redis;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * RedisStreamConsumer 동작 검증
 */
@DataRedisTest
class RedisStreamConsumerTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private TestStreamConsumer testStreamConsumer;

    private final String serverId = "server-1";
    private final String streamKey = "test-stream:server-id:" + serverId;
    private final String consumerGroup = "test-consumer-group";
    private final String consumerName = "test-consumer";

    @BeforeEach
    void setUp() {
        testStreamConsumer = new TestStreamConsumer(redisTemplate);
        testStreamConsumer.initConsumerGroup();
    }

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys("test-stream:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void 메시지를_읽고_acknowledge까지_정상처리한다() throws IOException {
        // given
        redisTemplate.opsForStream().add(streamKey, Map.of(
                "type", "test",
                "value", "ok"
        ));

        // when
        testStreamConsumer.consume();

        // then
        assertThat(testStreamConsumer.isHandled()).isTrue();
    }

    @Test
    void 메시지가_없으면_handleMessage_호출되지않는다() throws IOException {
        // when
        testStreamConsumer.consume();

        // then
        assertThat(testStreamConsumer.isHandled()).isFalse();
    }

    /**
     * RedisStreamConsumer를 테스트용으로 간단히 익명 구현
     */
    static class TestStreamConsumer extends RedisStreamConsumer {

        private final AtomicBoolean handled = new AtomicBoolean(false);

        protected TestStreamConsumer(StringRedisTemplate redisTemplate) {
            super(redisTemplate);
        }

        @Override
        protected String getConsumerGroupName() {
            return "test-consumer-group";
        }

        @Override
        protected String getConsumerName() {
            return "test-consumer";
        }

        @Override
        protected String getStreamKey() {
            return "test-stream:server-id:server-1";
        }

        @Override
        protected void handleMessage(MapRecord<String, Object, Object> message) {
            handled.set(true);
        }

        public boolean isHandled() {
            return handled.get();
        }
    }
}
