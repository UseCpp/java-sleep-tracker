package ru.yandex.practicum.sleeptracker.analyzer;

import ru.yandex.practicum.sleeptracker.session.SleepingSession;

import java.util.List;
import java.util.function.Function;

public interface SleepAnalyzer<R> extends Function<List<SleepingSession>, SleepAnalysisResult<R>> { }
