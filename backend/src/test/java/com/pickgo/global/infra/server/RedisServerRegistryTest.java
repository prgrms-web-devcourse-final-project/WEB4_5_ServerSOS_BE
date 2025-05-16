package com.pickgo.global.infra.server;

import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.pickgo.global.infra.server.redis.RedisServerRegistry;

/**
 * RedisServerRegistry 동작 검증
 */
@DataRedisTest
@Import({RedisServerRegistry.class})
class RedisServerRegistryTest {

    @Autowired
    private RedisServerRegistry serverRegistry;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final String connectionId = "conn-1";
    private final String serverId = "server-1";

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys("connection-server:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void 서버_식별자를_저장하고_정상조회한다() {
        // when
        serverRegistry.save(connectionId, serverId);

        // then
        String result = serverRegistry.getServerId(connectionId);
        assertThat(result).isEqualTo(serverId);
    }

    @Test
    void 서버_식별자_조회시_없으면_예외를_던진다() {
        assertThatThrownBy(() -> serverRegistry.getServerId(connectionId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(connectionId);
    }

    @Test
    void 서버_식별자를_제거하면_더이상_조회할수없다() {
        // given
        serverRegistry.save(connectionId, serverId);

        // when
        serverRegistry.remove(connectionId);

        // then
        assertThatThrownBy(() -> serverRegistry.getServerId(connectionId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 전체_데이터_초기화하면_모든_키가_삭제된다() {
        // given
        serverRegistry.save("conn-1", "server-1");
        serverRegistry.save("conn-2", "server-2");

        // when
        serverRegistry.clearAll();

        // then
        Set<String> keys = redisTemplate.keys("connection-server:*");
        assertThat(keys).isEmpty();
    }
}
