package com.pickgo.global.infra.stream.redis;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * RedisStreamPublisher 동작 검증
 */
@DataRedisTest
@Import({RedisStreamPublisher.class})
class RedisStreamPublisherTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisStreamPublisher publisher;

    private final String streamKey = "test-stream";

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys("test-stream:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void 스트림_메시지가_정상적으로_발행된다() {
        // given
        Map<String, String> data = Map.of("type", "TEST", "key", "value");

        // when
        publisher.publish(streamKey, data);

        // then
        var entries = redisTemplate.opsForStream().range(streamKey, Range.unbounded());
        assertThat(entries).isNotEmpty();
        assertThat(entries.getFirst().getValue())
                .containsEntry("type", "TEST")
                .containsEntry("key", "value");
    }
}
