package ru.yandex.practicum.sleeptracker.session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public class SleepingSessionLoader {
    public List<SleepingSession> load(String fileName) throws SleepingSessionLoadException {
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(fileName));
        } catch (IOException | InvalidPathException e) {
            throw new SleepingSessionLoadException("Не удалось прочитать данные из файла " + fileName);
        }

        try {
            return lines.stream()
                    .filter(Predicate.not(String::isBlank))
                    .map(SleepingSession::parseFrom)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new SleepingSessionLoadException(e.getMessage());
        }
    }
}
