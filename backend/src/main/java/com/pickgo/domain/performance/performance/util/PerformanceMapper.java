package com.pickgo.domain.performance.performance.util;

import com.pickgo.domain.performance.area.area.entity.AreaGrade;
import com.pickgo.domain.performance.area.area.entity.AreaName;
import com.pickgo.domain.performance.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.kopis.dto.KopisPerformanceDetailResponse;
import com.pickgo.domain.performance.performance.entity.*;
import com.pickgo.domain.performance.venue.entity.Venue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PerformanceMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    // 공연 생성
    public static Performance toPerformance(KopisPerformanceDetailResponse response, Venue venue) {
        Performance performance = Performance.builder()
                .name(response.getName())
                .startDate(LocalDate.parse(response.getStartDate(), DATE_TIME_FORMATTER))
                .endDate(LocalDate.parse(response.getEndDate(), DATE_TIME_FORMATTER))
                .runtime(response.getRuntime())
                .poster(response.getPoster())
                .state(convertState(response.getState()))
                .minAge(response.getMinAge())
                .casts(response.getCasts())
                .type(convertType(response.getType()))
                .venue(venue)
                .performanceIntros(toPerformanceIntros(response.getIntroImages()))
                .build();

        for (PerformanceIntro intro : performance.getPerformanceIntros()) {
            intro.setPerformance(performance);
        }

        List<PerformanceArea> areas = createPerformanceAreas(performance);
        performance.setPerformanceAreas(areas);

        List<PerformanceSession> sessions = toPerformanceSession(response.getSchedule(), performance);
        performance.setPerformanceSessions(sessions);

        return performance;
    }

    // 공연 상태 enum 변환
    private static PerformanceState convertState(String state) {
        return switch (state) {
            case "공연중" -> PerformanceState.ONGOING;
            case "공연완료" -> PerformanceState.COMPLETED;
            default -> PerformanceState.SCHEDULED;
        };
    }

    // 공연 타입 enum 변환
    private static PerformanceType convertType(String type) {
        return switch (type) {
            case "연극" -> PerformanceType.PLAY;
            case "무용" -> PerformanceType.DANCE;
            case "한국음악(국악)" -> PerformanceType.KOREAN;
            case "대중음악" -> PerformanceType.CONCERT;
            case "서양음악(클래식)" -> PerformanceType.CLASSIC;
            case "뮤지컬" -> PerformanceType.MUSICAL;
            default -> PerformanceType.ETC;
        };
    }

    // 소개 이미지 생성
    private static List<PerformanceIntro> toPerformanceIntros(List<String> introDtos) {
        return introDtos.stream()
                .map(introDto -> PerformanceIntro.builder()
                        .introImage(introDto)
                        .build())
                .toList();
    }

    // 회차 생성
    private static List<PerformanceSession> toPerformanceSession(String schedule, Performance performance) {
        Map<DayOfWeek, List<LocalTime>> scheduleMap = parseSchedule(schedule);

        List<PerformanceSession> sessions = new ArrayList<>();
        LocalDate date = performance.getStartDate();
        LocalDate endDate = performance.getEndDate();

        while (!date.isAfter(endDate)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            List<LocalTime> times = scheduleMap.get(dayOfWeek);
            if (times != null) {
                for (LocalTime time : times) {
                    LocalDateTime performanceTime = LocalDateTime.of(date, time);
                    LocalDateTime reserveOpenAt = performanceTime.minusDays(30);

                    sessions.add(PerformanceSession.builder()
                            .performance(performance)
                            .performanceTime(performanceTime)
                            .reserveOpenAt(reserveOpenAt)
                            .build());
                }
            }
            date = date.plusDays(1);
        }

        return sessions;
    }

    // 스케줄을 파싱하여 요일별 공연 시간을 얻는다.
    private static Map<DayOfWeek, List<LocalTime>> parseSchedule(String schedule) {
        Map<DayOfWeek, List<LocalTime>> map = new HashMap<>();

        schedule = schedule.replace(" ", "").replace("\n", "");

        String[] parts = schedule.split("\\),");

        for (String part : parts) {
            if (!part.endsWith(")")) {
                part += ")";
            }

            // 요일과 시간으로 분리
            String[] split = part.split("\\(");
            String daysPart = split[0];
            String timesPart = split[1].replace(")", "");

            // 시간 문자열을 LocalTime 리스트로 변환
            List<LocalTime> times = Arrays.stream(timesPart.split(","))
                    .map(PerformanceMapper::parseTime)
                    .toList();

            // 요일 문자열을 DayOfWeek 리스트로 변환: 월요일 ~ 수요일 -> [월, 화, 수]
            List<DayOfWeek> days = parseDays(daysPart);

            for (DayOfWeek day : days) {
                map.put(day, times);
            }
        }

        return map;
    }

    // 시간 문자열 -> LocalTime 변환
    private static LocalTime parseTime(String timeString) {
        String[] parts = timeString.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return LocalTime.of(hour, minute);
    }

    // 요일 문자열 -> DayOfWeek 리스트 변환
    private static List<DayOfWeek> parseDays(String daysPart) {
        List<DayOfWeek> days = new ArrayList<>();

        if (daysPart.contains("~")) {
            String[] range = daysPart.split("~");
            DayOfWeek start = koreanDayOfWeek(range[0]);
            DayOfWeek end = koreanDayOfWeek(range[1]);

            int startOrdinal = start.getValue();
            int endOrdinal = end.getValue();
            for (int i = startOrdinal; i <= endOrdinal; i++) {
                days.add(DayOfWeek.of(i));
            }
        } else {
            DayOfWeek dayOfWeek = koreanDayOfWeek(daysPart);
            if (dayOfWeek != null) {
                days.add(koreanDayOfWeek(daysPart));
            }
        }

        return days;
    }

    // 요일 -> DayOfWeek 변환
    private static DayOfWeek koreanDayOfWeek(String korean) {
        return switch (korean) {
            case "월요일" -> DayOfWeek.MONDAY;
            case "화요일" -> DayOfWeek.TUESDAY;
            case "수요일" -> DayOfWeek.WEDNESDAY;
            case "목요일" -> DayOfWeek.THURSDAY;
            case "금요일" -> DayOfWeek.FRIDAY;
            case "토요일" -> DayOfWeek.SATURDAY;
            case "일요일" -> DayOfWeek.SUNDAY;
            case "HOL" -> null;
            default -> throw new IllegalArgumentException("Unknown day: " + korean);
        };
    }

    private static class AreaConfig {
        AreaName areaName;
        AreaGrade areaGrade;
        int rowCount;
        int colCount;
        int price;

        public AreaConfig(AreaName areaName, AreaGrade areaGrade, int rowCount, int colCount, int price) {
            this.areaName = areaName;
            this.areaGrade = areaGrade;
            this.rowCount = rowCount;
            this.colCount = colCount;
            this.price = price;
        }
    }

    // 구역 생성
    private static List<PerformanceArea> createPerformanceAreas(Performance performance) {
        List<AreaConfig> areaConfigs = List.of(
                new AreaConfig(AreaName.VIP, AreaGrade.PREMIUM, 5, 20, 150000),
                new AreaConfig(AreaName.A, AreaGrade.SPECIAL, 15, 10, 100000),
                new AreaConfig(AreaName.B, AreaGrade.ROYAL, 15, 10, 120000),
                new AreaConfig(AreaName.C, AreaGrade.SPECIAL, 15, 10, 100000),
                new AreaConfig(AreaName.D, AreaGrade.NORMAL, 15, 30, 80000)
        );

        List<PerformanceArea> performanceAreas = new ArrayList<>();

        for (AreaConfig config : areaConfigs) {
            PerformanceArea area = PerformanceArea.builder()
                    .name(config.areaName)
                    .grade(config.areaGrade)
                    .price(config.price)
                    .rowCount(config.rowCount)
                    .colCount(config.colCount)
                    .performance(performance)
                    .build();

            performanceAreas.add(area);
        }

        return performanceAreas;
    }
}
