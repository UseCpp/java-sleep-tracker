package ru.yandex.practicum.sleeptracker.session;

public enum Quality {
    GOOD,
    NORMAL,
    BAD;

    public static Quality parseFrom(String value, String line) {
        try {
            return Quality.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестное качество сна в строке: " + line, e);
        }
    }
}
