package ru.yandex.practicum.sleeptracker.analyzer;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.session.SleepingSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BadSleepingSessionCounterTest {

    private final BadSleepingSessionCounter counter = new BadSleepingSessionCounter();

    @Test
    void shouldCountOnlyBadSessions() {
        var sessions = List.of(
                SleepingSession.parseFrom("01.10.25 22:15;02.10.25 08:00;BAD"),
                SleepingSession.parseFrom("02.10.25 23:00;03.10.25 08:00;NORMAL"),
                SleepingSession.parseFrom("03.10.25 14:30;03.10.25 15:20;GOOD"),
                SleepingSession.parseFrom("03.10.25 23:30;04.10.25 06:20;BAD"));

        assertEquals(2L, counter.apply(sessions).result());
    }

    @Test
    void shouldReturnZeroWhenNoBadSessions() {
        var sessions = List.of(
                SleepingSession.parseFrom("01.10.25 22:15;02.10.25 08:00;GOOD"),
                SleepingSession.parseFrom("02.10.25 23:00;03.10.25 08:00;NORMAL"));

        assertEquals(0L, counter.apply(sessions).result());
    }

    @Test
    void shouldReturnZeroForEmptyLog() {
        assertEquals(0L, counter.apply(List.of()).result());
    }
}
