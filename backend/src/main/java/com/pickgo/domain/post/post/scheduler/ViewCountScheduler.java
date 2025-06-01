package com.pickgo.domain.post.post.scheduler;

import com.pickgo.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ViewCountScheduler {
    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;

    // 3분 간격으로 DB에 조회수 반영
    @Transactional
    @Scheduled(fixedRate = 60000)
    public void updateViewCounts() {
        Set<String> keys = redisTemplate.keys("view_count:*");

        if (keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length != 2) {
                continue;
            }

            long id = Long.parseLong(parts[1]);
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                continue;
            }

            long viewCount = Long.parseLong(value);
            postRepository.updateViewCount(id, viewCount);

            redisTemplate.delete(key);
        }

    }
}
