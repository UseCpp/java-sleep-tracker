package ru.yandex.practicum.sleeptracker.analyzer;

import ru.yandex.practicum.sleeptracker.session.SleepingSession;

import java.util.List;

public class BadSleepingSessionCounter implements SleepAnalyzer<Long> {

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sleepingSessions) {
        long count = sleepingSessions.stream()
                .map(SleepingSession::quality)
                .filter(SleepingSession.Quality.BAD::equals)
                .count();
        return new SleepAnalysisResult<>("Количество сессий с плохим качеством сна", count);
    }
}
