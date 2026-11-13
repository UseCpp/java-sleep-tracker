package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analyzer.*;
import ru.yandex.practicum.sleeptracker.session.SleepingSession;
import ru.yandex.practicum.sleeptracker.session.SleepingSessionLoadException;
import ru.yandex.practicum.sleeptracker.session.SleepingSessionLoader;

import java.util.List;

public class SleepTrackerApp {

    private static final List<SleepAnalyzer<?>> ANALYZERS = List.of(
            new SleepingSessionCounter(),
            new SleepSessionDurationAnalyzer(SleepSessionDurationAnalyzer.MetricType.MIN),
            new SleepSessionDurationAnalyzer(SleepSessionDurationAnalyzer.MetricType.MAX),
            new SleepSessionDurationAnalyzer(SleepSessionDurationAnalyzer.MetricType.AVERAGE),
            new BadSleepingSessionCounter(),
            new SleeplessNightCounter(),
            new UserClassifierByNight()
    );

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Не указан путь до файла логов сна.");
            printEndProgram();
            return;
        }

        List<SleepingSession> sessions;
        try {
            sessions = new SleepingSessionLoader().load(args[0]);
        } catch (SleepingSessionLoadException e) {
            System.out.println("Ошибка в работе с файлом логов сна. " + e.getMessage());
            printEndProgram();
            return;
        }
        System.out.println("Анализируем сессии сна...");
        ANALYZERS.stream()
                .map(analyzer -> analyzer.apply(sessions))
                .forEach(sleepAnalysisResult -> {
                    System.out.println(sleepAnalysisResult.description());
                    System.out.println(sleepAnalysisResult.result());
                });
        printEndProgram();
    }

    private static void printEndProgram() {
        System.out.println("Программа завершена");
    }
}