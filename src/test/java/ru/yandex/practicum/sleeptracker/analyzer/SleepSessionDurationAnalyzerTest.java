package ru.yandex.practicum.sleeptracker.analyzer;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analyzer.SleepSessionDurationAnalyzer.MetricType;
import ru.yandex.practicum.sleeptracker.session.SleepingSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleepSessionDurationAnalyzerTest {

    public void assertSleepingSessionSeriesMetrics(List<SleepingSession> series, Number min, Number max, Number average) {
        assertEquals(average, new SleepSessionDurationAnalyzer(MetricType.AVERAGE).apply(series).result(), "Неправильное среднее значение");
        assertEquals(min, new SleepSessionDurationAnalyzer(MetricType.MIN).apply(series).result(), "Неправильное минимальное значение");
        assertEquals(max, new SleepSessionDurationAnalyzer(MetricType.MAX).apply(series).result(), "Неправильное максимальное значение");
    }

    @Test
    public void testSleepingSessionSeries() {
        var sessions = List.of(
                SleepingSession.parseFrom("01.10.25 22:15;02.10.25 08:00;GOOD"),   //585 минут
                SleepingSession.parseFrom("02.10.25 23:00;03.10.25 08:00;NORMAL"), //540 минут
                SleepingSession.parseFrom("03.10.25 14:30;03.10.25 15:20;NORMAL"), //50 минут
                SleepingSession.parseFrom("03.10.25 23:30;04.10.25 06:20;BAD"));   //410 минут
        assertSleepingSessionSeriesMetrics(sessions, 50L, 585L, 396.25);
    }

    @Test
    public void testSingleSession() {
        assertSleepingSessionSeriesMetrics(
                List.of(SleepingSession.parseFrom("01.10.25 23:00;02.10.25 07:00;GOOD")),
                480L,
                480L,
                480.0
        );
    }

    @Test
    public void testNoSessions() {
        assertSleepingSessionSeriesMetrics(List.of(), 0L, 0L, 0.0);
    }

    @Test
    void shouldCountDurationAcrossMonthBoundary() {
        var sessions = List.of(SleepingSession.parseFrom("31.10.25 23:30;01.11.25 07:30;GOOD"));
        assertEquals(480L, new SleepSessionDurationAnalyzer(MetricType.MAX).apply(sessions).result());
    }
}
