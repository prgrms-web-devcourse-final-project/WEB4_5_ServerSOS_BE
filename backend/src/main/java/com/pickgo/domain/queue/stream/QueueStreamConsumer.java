package com.pickgo.domain.queue.stream;

import java.io.IOException;
import java.util.List;

import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pickgo.domain.queue.dto.EntryPermission;
import com.pickgo.domain.queue.dto.WaitingState;
import com.pickgo.global.init.ServerIdProvider;
import com.pickgo.global.sse.SseHandler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueStreamConsumer {

    private static final String GROUP_NAME = "queue-consumer-group";
    private static final int READ_COUNT = 10; // 한번에 처리할 메시지 개수
    private final StringRedisTemplate redisTemplate;
    private final ServerIdProvider serverIdProvider;
    private final SseHandler sseHandler;

    /**
     * Consumer Group 기반 메시지 소비
     */
    @Scheduled(fixedRate = 100)
    public void consume() throws IOException {
        String serverId = serverIdProvider.getServerId();
        String streamKey = getStreamKey(serverId);

        // 메시지 가져옴
        var messages = getMessages(serverId, streamKey);

        // 메시지가 없으면 종료
        if (messages == null || messages.isEmpty()) {
            return;
        }

        // 메시지 처리 및 ACK 표시
        for (var message : messages) {
            handleMessage(message);
            redisTemplate.opsForStream().acknowledge(streamKey, GROUP_NAME, message.getId());
        }
    }

    /**
     * 메시지 조회
     */
    @SuppressWarnings("unchecked")
    private List<MapRecord<String, Object, Object>> getMessages(String serverId, String streamKey) {
        try {
            // Stream에서 Consumer Group이 처리할 메시지 가져옴
            return redisTemplate.opsForStream()
                    .read(Consumer.from(GROUP_NAME, serverId),
                            StreamReadOptions.empty().count(READ_COUNT),
                            StreamOffset.create(streamKey, ReadOffset.lastConsumed()));
        } catch (Exception e) {
            log.warn("Failed to read messages from Redis stream: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 메시지 처리
     */
    private void handleMessage(MapRecord<String, Object, Object> message) throws IOException {
        var values = message.getValue();

        String type = values.get("type").toString();
        String connectionId = values.get("connection_id").toString();

        switch (type) {
            case "ready" -> { // 대기 완료, 입장 준비됨
                String entryToken = values.get("entry_token").toString();
                sseHandler.sendMessage(type, connectionId, EntryPermission.of(entryToken));
                sseHandler.complete(connectionId);
            }
            case "wait" -> { // 대기열 상태
                int position = Integer.parseInt(values.get("position").toString());
                int totalCount = Integer.parseInt(values.get("total_count").toString());
                String estimatedTime = values.get("estimated_time").toString();
                sseHandler.sendMessage(type, connectionId, WaitingState.of(position, totalCount, estimatedTime));
            }
        }
    }

    /**
     * Consumer Group 생성
     */
    @PostConstruct
    public void initConsumerGroup() {
        String serverId = serverIdProvider.getServerId();
        String streamKey = getStreamKey(serverId);

        try {
            redisTemplate.opsForStream().createGroup(streamKey, GROUP_NAME);
        } catch (Exception e) {
            log.warn("Failed to create consumer group: ", e);
        }
    }

    private static String getStreamKey(String serverId) {
        return "queue_stream:serverId:" + serverId;
    }
}

