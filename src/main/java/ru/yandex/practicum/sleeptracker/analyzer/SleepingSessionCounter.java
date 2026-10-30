package ru.yandex.practicum.sleeptracker.analyzer;

import ru.yandex.practicum.sleeptracker.session.SleepingSession;

import java.util.List;

public class SleepingSessionCounter implements SleepAnalyzer<Integer> {
    @Override
    public SleepAnalysisResult<Integer> apply(List<SleepingSession> sleepingSessions) {
        return new SleepAnalysisResult<>("Количество сессий сна", sleepingSessions.size());
    }
}
