package com.pickgo.global.infra.stream.redis;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis Stream 메시지를 소비하는 Consumer
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class RedisStreamConsumer {

    protected final StringRedisTemplate redisTemplate;
    private final Environment environment;

    /**
     * 초기화 시 Consumer Group 생성 후, 메시지 소비 루프 실행
     */
    @PostConstruct
    public void init() {
        // test 환경에서는 초기화하지 않는다.
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return;
        }

        initConsumerGroup();
        CompletableFuture.runAsync(this::consumeLoop, getExecutor());
    }

    /**
     * 무한 루프를 돌며 Redis Stream 메시지를 blocking 방식으로 소비
     */
    private void consumeLoop() {
        String consumerGroup = getConsumerGroupName();
        String consumerName = getConsumerName();
        String streamKey = getStreamKey();

        while (true) {
            consume(consumerGroup, consumerName, streamKey);
        }
    }

    /**
     * 메시지 소비
     */
    public void consume(String consumerGroup, String consumerName, String streamKey) {
        try {
            List<MapRecord<String, Object, Object>> messages = getMessages(consumerGroup, consumerName, streamKey);

            if (messages == null || messages.isEmpty()) {
                return;
            }

            // 메시지 처리 비동기로 수행
            messages.forEach(message -> CompletableFuture.runAsync(() -> {
                try {
                    // 메시지 처리
                    handleMessage(message);
                } catch (IOException e) {
                    // 연결이 끊겨서 메시지를 못보낸 경우
                    log.warn("Error handling message: {}", e.getMessage());
                }
                // 연결이 끊겨서 보내지 못한 메시지는 다시 보내지 못하므로 ACK 처리
                redisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, message.getId());
            }, getExecutor()));

        } catch (Exception e) {
            log.error("Error while consuming Redis Stream: {}", e.getMessage(), e);
        }
    }

    /**
     * Stream에서 메시지를 조회
     */
    @SuppressWarnings("unchecked")
    private List<MapRecord<String, Object, Object>> getMessages(String consumerGroup, String consumerName,
            String streamKey) {
        try {
            return redisTemplate.opsForStream()
                    .read(Consumer.from(consumerGroup, consumerName),
                            StreamReadOptions.empty()
                                    .block(Duration.ofSeconds(2)) // 최대 2초 대기 후 응답
                                    .count(getReadCount()),
                            StreamOffset.create(streamKey, ReadOffset.lastConsumed()));
        } catch (Exception e) {
            log.warn("Failed to get messages from Redis Stream: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Consumer Group 생성
     */
    public void initConsumerGroup() {
        String streamKey = getStreamKey();
        String consumerGroup = getConsumerGroupName();
        try {
            redisTemplate.opsForStream().createGroup(streamKey, consumerGroup);
        } catch (Exception ignored) {
        }
    }

    /**
     * 한 번에 읽는 메시지 수
     */
    protected int getReadCount() {
        return 1000;
    }

    protected abstract void handleMessage(MapRecord<String, Object, Object> message) throws IOException;

    protected abstract String getConsumerGroupName();

    protected abstract String getConsumerName();

    protected abstract String getStreamKey();

    protected abstract Executor getExecutor();
}
