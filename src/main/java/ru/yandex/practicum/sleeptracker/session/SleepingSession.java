package ru.yandex.practicum.sleeptracker.session;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public record SleepingSession(LocalDateTime bedtime, LocalDateTime wakeup, Quality quality) {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public enum Quality {
        GOOD,
        NORMAL,
        BAD
    }

    /// Разбор строки формата `dd.MM.yy HH:mm;dd.MM.yy HH:mm;QUALITY`
    ///
    /// @throws IllegalArgumentException если строку невозможно разобрать
    public static SleepingSession parseFrom(String line) {
        String[] parts = line.trim().split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Невозможно распарсить строку: " + line);
        }
        var bedtime = parseDateTime(parts[0], line);
        var wakeup = parseDateTime(parts[1], line);
        var quality = parseQuality(parts[2], line);
        if (wakeup.isBefore(bedtime)) {
            throw new IllegalArgumentException("Время пробуждения раньше времени засыпания: " + line);
        }
        return new SleepingSession(bedtime, wakeup, quality);
    }

    private static LocalDateTime parseDateTime(String value, String line) {
        try {
            return LocalDateTime.parse(value.trim(), FORMATTER);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Неверный формат даты в строке: " + line, e);
        }
    }

    private static Quality parseQuality(String value, String line) {
        try {
            return Quality.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестное качество сна в строке: " + line, e);
        }
    }

    /// Продолжительность сессии в минутах
    public long durationInMinutes() {
        return Duration.between(bedtime, wakeup).toMinutes();
    }

    /// Проверка, что сон был в одном дне
    public boolean isOneDaySession() {
        return bedtime.toLocalDate().equals(wakeup.toLocalDate());
    }

    /// Сессия дневного сна - засыпание после 6:00 и сон в пределах одного дня
    public boolean isDaySleeping() {
        return isOneDaySession() && bedtime.getHour() >= 6;
    }

    /// Возвращает дату, если сон был ночью (ночь - дата, в которой был сон между 00:00 и 06:00)
    public Optional<LocalDate> getSleepingNight() {
        return isDaySleeping() ? Optional.empty() : Optional.of(wakeup.toLocalDate());
    }
}
