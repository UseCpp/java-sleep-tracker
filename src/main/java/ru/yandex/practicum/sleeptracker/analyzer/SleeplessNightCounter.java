package ru.yandex.practicum.sleeptracker.analyzer;

import ru.yandex.practicum.sleeptracker.session.SleepingSession;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class SleeplessNightCounter implements SleepAnalyzer<Long> {

    private static final String DESCRIPTION = "Количество бессонных ночей за время трекинга";

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sleepingSessions) {
        if (sleepingSessions.isEmpty()) {
            return new SleepAnalysisResult<>(DESCRIPTION, 0L);
        }

        // Если трекинг начался до полудня, учитываем предыдущую ночь, иначе - следующую.
        var trackingStart = sleepingSessions.getFirst().bedtime();
        var firstNight = trackingStart.toLocalDate();
        if (!trackingStart.toLocalTime().isBefore(LocalTime.NOON)) {
            firstNight = firstNight.plusDays(1);
        }
        var lastNight = sleepingSessions.getLast().wakeup().toLocalDate();

        if (firstNight.isAfter(lastNight)) {
            return new SleepAnalysisResult<>(DESCRIPTION, 0L);
        }
        long nights = ChronoUnit.DAYS.between(firstNight, lastNight) + 1;

        var sleepingNights = sleepingSessions.stream()
                .map(SleepingSession::getSleepingNight)
                .flatMap(Optional::stream)
                .distinct()
                .count();

        return new SleepAnalysisResult<>(DESCRIPTION, nights - sleepingNights);
    }
}
