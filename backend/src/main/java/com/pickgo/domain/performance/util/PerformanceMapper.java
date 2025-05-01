package com.pickgo.domain.performance.util;

import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.area.seat.entity.Seat;
import com.pickgo.domain.area.seat.entity.SeatStatus;
import com.pickgo.domain.kopis.dto.KopisPerformanceDetailResponse;
import com.pickgo.domain.performance.entity.*;

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
                .productionCompany(response.getProductionCompany())
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
        createPerformanceSessions(sessions, areas);
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
                        .intro_image(introDto)
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
                    sessions.add(PerformanceSession.builder()
                            .performance(performance)
                            .performanceTime(LocalDateTime.of(date, time))
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

    // 구역 생성
    private static List<PerformanceArea> createPerformanceAreas(Performance performance) {
        List<PerformanceArea> performanceAreas = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            PerformanceArea performanceArea = PerformanceArea.builder()
                    .name("구역 " + i)
                    .price(100000)
                    .performance(performance)
                    .build();

            List<Seat> seats = createSeats(performanceArea);
            performanceArea.setSeats(seats);

            performanceAreas.add(performanceArea);
        }

        return performanceAreas;
    }

    // 좌석 생성 및 구역과 연결
    private static List<Seat> createSeats(PerformanceArea area) {
        List<Seat> seats = new ArrayList<>();

        for (char row = 'A'; row <= 'Z'; row++) { // A ~ Z
            for (int number = 1; number <= 20; number++) { // 1 ~ 20
                seats.add(Seat.builder()
                        .row(String.valueOf(row))
                        .number(number)
                        .performanceArea(area)
                        .status(SeatStatus.AVAILABLE)
                        .build());
            }
        }

        return seats;
    }

    // 회차 별 좌석 생성 및 구역과 연결
    private static void createPerformanceSessions(List<PerformanceSession> sessions, List<PerformanceArea> areas) {
        for (PerformanceSession session : sessions) {
            List<Seat> sessionSeats = new ArrayList<>();
            for (PerformanceArea area : areas) {
                for (Seat seat : area.getSeats()) {
                    sessionSeats.add(Seat.builder()
                            .row(seat.getRow())
                            .number(seat.getNumber())
                            .performanceSession(session)
                            .performanceArea(area)
                            .status(SeatStatus.AVAILABLE)
                            .build());
                    seat.setPerformanceSession(session);
                }
            }
            session.setSeats(sessionSeats);
        }
    }
}
