package com.pickgo.domain.queue.policy;

import org.springframework.stereotype.Component;

import com.pickgo.global.infra.metric.repository.redis.RedisMetricRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntryCountDecider {

    private final RedisMetricRepository redisMetricsRepository;

    public int decideEntryCount() {
        Long tps = redisMetricsRepository.getLatestTps();

        if (tps == null) {
            return 0;
        }

        return interpolate(tps, 0, 100, 50, 1);
    }

    /**
     * 선형 보간으로 입장 인원 수 계산
     */
    private int interpolate(long tps, long minTps, long maxTps, int maxCnt, int minCnt) {
        // TPS가 범위 밖일 경우 최대/최소값
        if (tps <= minTps)
            return maxCnt;
        if (tps >= maxTps)
            return minCnt;

        // 비율 계산
        double ratio = (double)(tps - minTps) / (maxTps - minTps); // 현재 TPS가 어느 위치인지에 대한 비율
        return (int)Math.round(minCnt + ratio * (maxCnt - minCnt)); // 해당 비율에 따라 입장 인원 계산
    }
}
