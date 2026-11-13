package ru.yandex.practicum.sleeptracker.analyzer;

import ru.yandex.practicum.sleeptracker.session.SleepingSession;

import java.util.List;
import java.util.Objects;

public class SleepSessionDurationAnalyzer implements SleepAnalyzer<Number> {

    private static final String DESCRIPTION_FORMAT = "%s продолжительность сна в минутах";

    public enum MetricType {
        MIN("Минимальная"),
        MAX("Максимальная"),
        AVERAGE("Средняя");

        private final String toDescription;

        MetricType(String toDescription) {
            this.toDescription = toDescription;
        }

        private String toDescription() {
            return toDescription;
        }
    }

    private final MetricType metricType;

    public SleepSessionDurationAnalyzer(MetricType metricType) {
        this.metricType = Objects.requireNonNull(metricType, "Не указан тип функции анализа продолжительности сна");
    }

    @Override
    public SleepAnalysisResult<Number> apply(List<SleepingSession> sleepingSessions) {
        var sleepingSessionDurationStream = sleepingSessions
                .stream()
                .mapToLong(SleepingSession::durationInMinutes);

        Number result = switch (metricType) {
            case MAX -> sleepingSessionDurationStream.max().orElse(0);
            case MIN -> sleepingSessionDurationStream.min().orElse(0);
            case AVERAGE -> sleepingSessionDurationStream.average().orElse(0);
        };
        return new SleepAnalysisResult<>(String.format(DESCRIPTION_FORMAT, metricType.toDescription()), result);
    }
}
