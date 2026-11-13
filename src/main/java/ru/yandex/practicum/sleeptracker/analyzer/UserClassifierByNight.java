package ru.yandex.practicum.sleeptracker.analyzer;

import ru.yandex.practicum.sleeptracker.session.SleepingSession;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserClassifierByNight implements SleepAnalyzer<UserClassifierByNight.UserClassification> {

    public enum UserClassification {
        OWL("Сова"),
        LARK("Жаворонок"),
        PIGEON("Голубь"),
        UNDEFINED("Невозможно определить");

        private static final LocalTime OWL_BEDTIME = LocalTime.of(23, 0);
        private static final LocalTime OWL_WAKEUP = LocalTime.of(9, 0);
        private static final LocalTime LARK_BEDTIME = LocalTime.of(22, 0);
        private static final LocalTime LARK_WAKEUP = LocalTime.of(7, 0);

        private final String name;

        UserClassification(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        /// Тип ночи по времени засыпания и пробуждения (пробуждение - утро этой ночи)
        public static UserClassification byNight(LocalDateTime bedtime, LocalDateTime wakeup) {
            boolean bedtimeAfterMidnight = bedtime.toLocalDate().equals(wakeup.toLocalDate());

            boolean lateBedtime = bedtimeAfterMidnight || bedtime.toLocalTime().isAfter(OWL_BEDTIME);
            if (lateBedtime && wakeup.toLocalTime().isAfter(OWL_WAKEUP)) {
                return OWL;
            }

            boolean earlyBedtime = !bedtimeAfterMidnight && bedtime.toLocalTime().isBefore(LARK_BEDTIME);
            if (earlyBedtime && wakeup.toLocalTime().isBefore(LARK_WAKEUP)) {
                return LARK;
            }

            return PIGEON;
        }

        ///  Тип пользователя по всем сессиям сна в пределах ночи
        public static UserClassification byAllNightSession(List<SleepingSession> sessionsPerNight) {
            return byNight(
                    sessionsPerNight.stream().map(SleepingSession::bedtime).min(Comparator.naturalOrder()).orElseThrow(),
                    sessionsPerNight.stream().map(SleepingSession::wakeup).max(Comparator.naturalOrder()).orElseThrow()
            );
        }
    }

    @Override
    public SleepAnalysisResult<UserClassification> apply(List<SleepingSession> sleepingSessions) {
        // Может быть такое, что человек засыпал и просыпался в пределах ночи,
        // Будем считать тогда сессией сна то разницу между самым ранним засыпанием
        // и самым поздним пробуждением
        Map<UserClassification, Long> nightsByType = sleepingSessions.stream()
                .filter(Predicate.not(SleepingSession::isDaySleeping))
                .collect(Collectors.groupingBy(session -> session.wakeup().toLocalDate()))
                .values()
                .stream()
                .map(UserClassification::byAllNightSession)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return new SleepAnalysisResult<>("Хронотип пользователя", mostFrequent(nightsByType));
    }

    private static UserClassification mostFrequent(Map<UserClassification, Long> nightsByType) {
        if (nightsByType.isEmpty()) {
            return UserClassification.UNDEFINED;
        }
        long maxCount = nightsByType.values().stream().max(Comparator.naturalOrder()).orElseThrow();
        List<UserClassification> leaders = nightsByType.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .toList();
        // При равенстве нескольких типов считаем пользователя голубем
        return leaders.size() == 1 ? leaders.getFirst() : UserClassification.PIGEON;
    }
}
