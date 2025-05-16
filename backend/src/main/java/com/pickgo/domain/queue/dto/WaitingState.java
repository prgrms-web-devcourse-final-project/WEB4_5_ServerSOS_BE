package com.pickgo.domain.queue.dto;

public record WaitingState(
        int position, // 대기번호
        int totalCount, // 총 대기인원
        String estimatedTime // 예상 대기시간
) {
    public static WaitingState of(int position, int totalCount, double tps) {
        // tps 기준으로 예상 대기시간(초) 계산
        int remainingSeconds = (int)Math.ceil(position / tps); // 최소 1초 이상

        int hours = remainingSeconds / 3600;
        int minutes = (remainingSeconds % 3600) / 60;
        int seconds = remainingSeconds % 60;

        StringBuilder estimatedTimeBuilder = new StringBuilder();
        if (hours > 0) {
            estimatedTimeBuilder.append(hours).append("시간 ");
        }
        if (minutes > 0 || hours > 0) {
            estimatedTimeBuilder.append(minutes).append("분 ");
        }
        estimatedTimeBuilder.append(seconds).append("초");

        String estimatedTime = estimatedTimeBuilder.toString().trim();

        return new WaitingState(position, totalCount, estimatedTime);
    }

    public static WaitingState of(int position, int totalCount, String estimatedTime) {
        return new WaitingState(position, totalCount, estimatedTime);
    }
}
