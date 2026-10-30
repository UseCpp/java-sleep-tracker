package ru.yandex.practicum.sleeptracker.analyzer;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.session.SleepingSession;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleepingSessionCounterTest {

    private final SleepingSessionCounter counter = new SleepingSessionCounter();

    private static final SleepingSession DUMMY_SESSION = new SleepingSession(LocalDateTime.now(), LocalDateTime.now().plusDays(1), SleepingSession.Quality.GOOD);

    @Test
    void shouldReturnZeroForEmptyLog() {
        assertEquals(0, counter.apply(List.of()).result());
    }

    @Test
    void shouldCountSingleSession() {
        assertEquals(1, counter.apply(List.of(DUMMY_SESSION)).result());
    }

    @Test
    void shouldCountAllSessions() {
        int size = 4;
        var sessions = Collections.nCopies(size, DUMMY_SESSION);
        assertEquals(size, counter.apply(sessions).result());
    }
}
