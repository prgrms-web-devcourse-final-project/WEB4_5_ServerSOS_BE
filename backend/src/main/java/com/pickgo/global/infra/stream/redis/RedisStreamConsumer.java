package com.pickgo.global.infra.stream.redis;

import java.io.IOException;
import java.util.List;

import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.annotation.PostConstruct;

/**
 * Redis Stream 메시지를 소비하는 Consumer
 */
public abstract class RedisStreamConsumer {

    protected final StringRedisTemplate redisTemplate;

    protected RedisStreamConsumer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Consumer Group 기반 메시지 소비
     */
    @Scheduled(fixedRate = 100)
    public void consume() throws IOException {
        String consumerGroup = getConsumerGroupName();
        String consumerName = getConsumerName();
        String streamKey = getStreamKey();

        var messages = getMessages(consumerGroup, consumerName, streamKey);

        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (var message : messages) {
            handleMessage(message);
            redisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, message.getId());
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
                            StreamReadOptions.empty().count(getReadCount()),
                            StreamOffset.create(streamKey, ReadOffset.lastConsumed()));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Consumer Group 생성
     */
    @PostConstruct
    public void initConsumerGroup() {
        String streamKey = getStreamKey();
        String consumerGroup = getConsumerGroupName();
        try {
            redisTemplate.opsForStream().createGroup(streamKey, consumerGroup);
        } catch (Exception ignored) {
        }
    }

    /**
     * 한번에 읽는 메시지 수
     */
    protected int getReadCount() {
        return 10;
    }

    protected abstract String getConsumerGroupName();

    protected abstract String getConsumerName();

    protected abstract String getStreamKey();

    protected abstract void handleMessage(MapRecord<String, Object, Object> message) throws IOException;
}