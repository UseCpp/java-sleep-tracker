package ru.yandex.practicum.sleeptracker.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SleepingSessionLoaderTest {

    @TempDir
    Path dir;

    private final SleepingSessionLoader loader = new SleepingSessionLoader();

    private Path createLog(String content) throws IOException {
        var file = dir.resolve("sleep_log.txt");
        return Files.writeString(file, content);
    }

    @Test
    void shouldLoadSessionsIgnoringSpacesAndBlankLines() throws Exception {
        Path file = createLog("01.10.25 22:15;02.10.25 08:00;GOOD\n\n03.10.25 23:30;04.10.25 06:20;BAD \n");

        var sessions = loader.load(file.toString());

        assertEquals(2, sessions.size());
        assertEquals(new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 22, 15),
                LocalDateTime.of(2025, 10, 2, 8, 0),
                SleepingSession.Quality.GOOD), sessions.getFirst());

        assertEquals(new SleepingSession(
                LocalDateTime.of(2025, 10, 3, 23, 30),
                LocalDateTime.of(2025, 10, 4, 6, 20),
                SleepingSession.Quality.BAD), sessions.getLast());
    }

    @Test
    void shouldFailOnMissingFile() {
        assertThrows(SleepingSessionLoadException.class, () -> loader.load("not_existing_file.txt"));
    }

    @Test
    void shouldFailOnUnknownQuality() throws Exception {
        Path file = createLog("01.10.25 22:15;02.10.25 08:00;EXCELLENT\n");

        var exception = assertThrows(SleepingSessionLoadException.class, () -> loader.load(file.toString()));
        assertEquals("Неизвестное качество сна в строке: 01.10.25 22:15;02.10.25 08:00;EXCELLENT",
                exception.getMessage());
    }

    @Test
    void shouldFailOnInvalidDate() throws Exception {
        Path file = createLog("01.10.2025 22:15;02.10.25 08:00;GOOD\n");

        assertThrows(SleepingSessionLoadException.class, () -> loader.load(file.toString()));
    }

    @Test
    void shouldFailWhenWakeupBeforeBedtime() throws Exception {
        Path file = createLog("02.10.25 08:00;01.10.25 22:15;GOOD\n");

        assertThrows(SleepingSessionLoadException.class, () -> loader.load(file.toString()));
    }
}
