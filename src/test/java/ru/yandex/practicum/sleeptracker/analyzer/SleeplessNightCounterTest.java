package ru.yandex.practicum.sleeptracker.analyzer;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.session.SleepingSession;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleeplessNightCounterTest {

    private final SleeplessNightCounter counter = new SleeplessNightCounter();

    private long count(String... lines) {
        return counter.apply(Stream.of(lines).map(SleepingSession::parseFrom).toList()).result();
    }

    @Test
    void shouldReturnZeroWhenEveryNightHasSleep() {
        assertEquals(0, count(
                "01.10.25 22:15;02.10.25 08:00;GOOD",
                "02.10.25 23:00;03.10.25 08:00;NORMAL",
                "03.10.25 14:30;03.10.25 15:20;NORMAL",
                "03.10.25 23:30;04.10.25 06:20;BAD"));
    }

    @Test
    void shouldCountNightsWithoutSleep() {
        // ночи на 03.10 и 04.10 пропущены, на 05.10 - только дневной сон
        assertEquals(3, count(
                "01.10.25 22:00;02.10.25 07:00;GOOD",
                "05.10.25 07:00;05.10.25 11:00;NORMAL",
                "05.10.25 22:00;06.10.25 07:00;GOOD"));
    }

    @Test
    void shouldTreatSleepEndingOrStartingInsideNightIntervalAsNightSleep() {
        // 23:00-03:00 и 02:00-07:00 пересекают интервал 0:00-6:00
        assertEquals(0, count(
                "01.10.25 23:00;02.10.25 03:00;GOOD",
                "03.10.25 02:00;03.10.25 07:00;GOOD"));
    }

    @Test
    void shouldCountNightsAcrossMonthBoundary() {
        // ночи с 02.10 по 03.11 - всего 33, из них со сном только 2
        assertEquals(31, count(
                "01.10.25 22:00;02.10.25 07:00;GOOD",
                "02.11.25 22:00;03.11.25 07:00;GOOD"));
    }

    @Test
    void shouldIncludePreviousNightWhenTrackingStartsBeforeNoon() {
        // лог начинается в 10:00, поэтому ночь на 01.10 тоже учитывается и она бессонная
        assertEquals(1, count(
                "01.10.25 10:00;01.10.25 11:00;NORMAL",
                "01.10.25 23:00;02.10.25 07:00;GOOD"));
    }

    @Test
    void shouldStartFromNextNightWhenTrackingStartsAfterNoon() {
        assertEquals(0, count(
                "01.10.25 12:30;01.10.25 13:00;NORMAL",
                "01.10.25 23:00;02.10.25 07:00;GOOD"));
    }

    @Test
    void shouldStartFromNextNightWhenTrackingStartsExactlyAtNoon() {
        assertEquals(0, count(
                "01.10.25 12:00;01.10.25 13:00;NORMAL",
                "01.10.25 23:00;02.10.25 07:00;GOOD"));
    }

    @Test
    void shouldCountNightsAcrossYearBoundary() {
        // ночи с 31.12 по 03.01 - всего 4, бессонная только ночь на 02.01
        assertEquals(1, count(
                "30.12.25 22:00;31.12.25 07:00;GOOD",
                "31.12.25 23:00;01.01.26 08:00;GOOD",
                "02.01.26 23:00;03.01.26 07:00;GOOD"));
    }

    @Test
    void shouldReturnZeroForSingleNightSessionWithinOneDay() {
        assertEquals(0, count("01.10.25 03:00;01.10.25 05:00;GOOD"));
    }

    @Test
    void shouldReturnZeroForSingleAfternoonNap() {
        assertEquals(0, count("01.10.25 14:00;01.10.25 15:00;GOOD"));
    }

    @Test
    void shouldCountNightWithSeveralSessionsOnce() {
        assertEquals(0, count(
                        "01.10.25 23:00;02.10.25 02:00;BAD",
                        "02.10.25 03:00;02.10.25 07:00;NORMAL"
                )
        );
    }

    @Test
    void shouldReturnZeroForEmptyLog() {
        assertEquals(0L, counter.apply(List.of()).result());
    }
}
