package com.pickgo.global.infra.stream.redis;

import static com.pickgo.global.infra.stream.redis.RedisStreamConsumerTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.pickgo.global.config.thread.ExecutorConfig;

/**
 * RedisStreamConsumer 동작 검증
 */
@DataRedisTest
@Import({ExecutorConfig.class, TestStreamConsumer.class})
class RedisStreamConsumerTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TestStreamConsumer testStreamConsumer;

    private String streamKey;
    private String consumerGroup;
    private String consumerName;

    @BeforeEach
    void setUp() {
        streamKey = testStreamConsumer.getStreamKey();
        consumerGroup = testStreamConsumer.getConsumerGroupName();
        consumerName = testStreamConsumer.getConsumerName();

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
    void 메시지를_읽고_acknowledge까지_정상처리한다() {
        // given
        String streamKey = testStreamConsumer.getStreamKey();
        redisTemplate.opsForStream().add(streamKey, Map.of(
                "type", "test",
                "value", "ok"
        ));

        // when
        testStreamConsumer.consume(consumerGroup, consumerName, streamKey);

        // then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(testStreamConsumer.isHandled()).isTrue()
        );
    }

    @Test
    void 메시지가_없으면_handleMessage_호출되지않는다() {
        // when
        testStreamConsumer.consume(consumerGroup, consumerName, streamKey);

        // then
        assertThat(testStreamConsumer.isHandled()).isFalse();
    }

    /**
     * RedisStreamConsumer를 테스트용으로 간단히 익명 구현
     */
    @Component
    @Profile("test")
    static class TestStreamConsumer extends RedisStreamConsumer {

        @Autowired
        private final ExecutorConfig executorConfig;

        private final AtomicBoolean handled = new AtomicBoolean(false);

        protected TestStreamConsumer(StringRedisTemplate redisTemplate, ExecutorConfig executorConfig,
                Environment environment) {
            super(redisTemplate, environment);
            this.executorConfig = executorConfig;
        }

        @Override
        protected String getConsumerGroupName() {
            return "test-consumer-group";
        }

        @Override
        protected String getConsumerName() {
            return "server-1";
        }

        @Override
        protected String getStreamKey() {
            return "test-stream:server-id:server-1";
        }

        @Override
        protected ThreadPoolTaskExecutor getExecutor() {
            return executorConfig.threadPoolTaskExecutor();
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
