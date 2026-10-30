package ru.yandex.practicum.sleeptracker.analyzer;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analyzer.UserClassifierByNight.UserClassification;
import ru.yandex.practicum.sleeptracker.session.SleepingSession;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserClassifierByNightTest {

    private final UserClassifierByNight classifier = new UserClassifierByNight();

    private UserClassification classify(String... lines) {
        return classifier.apply(Stream.of(lines).map(SleepingSession::parseFrom).toList()).result();
    }

    @Test
    void shouldClassifyOwl() {
        assertEquals(UserClassification.OWL, classify(
                "01.10.25 23:30;02.10.25 09:30;GOOD",
                "03.10.25 00:30;03.10.25 10:00;GOOD",
                "03.10.25 22:30;04.10.25 08:00;GOOD")
        );
    }

    @Test
    void shouldClassifyLark() {
        assertEquals(UserClassification.LARK, classify(
                "01.10.25 21:00;02.10.25 06:00;GOOD",
                "02.10.25 21:30;03.10.25 06:30;GOOD",
                "03.10.25 22:30;04.10.25 08:00;GOOD")
        );
    }

    @Test
    void shouldClassifyPigeon() {
        assertEquals(UserClassification.PIGEON, classify(
                "01.10.25 22:30;02.10.25 08:00;GOOD",
                "02.10.25 23:30;03.10.25 08:30;GOOD",
                "03.10.25 21:00;04.10.25 09:30;GOOD")
        );
    }

    @Test
    void shouldNotTreatEarlyBedtimeWithLateWakeupAsOwl() {
        assertEquals(UserClassification.PIGEON, classify("01.10.25 21:00;02.10.25 09:30;GOOD"));
    }

    @Test
    void shouldReturnPigeonOnTie() {
        assertEquals(UserClassification.PIGEON, classify(
                "01.10.25 23:30;02.10.25 09:30;GOOD",
                "02.10.25 21:00;03.10.25 06:00;GOOD")
        );
    }

    @Test
    void shouldIgnoreDaySessions() {
        // дневной сон 07:00-11:00 по времени похож на "сову", но не должен учитываться
        assertEquals(UserClassification.LARK, classify(
                "01.10.25 21:00;02.10.25 06:00;GOOD",
                "02.10.25 07:00;02.10.25 11:00;NORMAL",
                "02.10.25 14:00;02.10.25 15:00;NORMAL")
        );
    }

    @Test
    void shouldClassifyNightBySeveralSessionsAsOneNight() {
        // одна ночь 23:30-10:00 из двух сессий - сова; иначе была бы ничья
        assertEquals(UserClassification.OWL,
                classify(
                        "01.10.25 23:30;02.10.25 03:00;GOOD",
                        "02.10.25 03:30;02.10.25 10:00;GOOD")
        );
    }

    @Test
    void shouldIgnoreSleeplessNights() {
        // пропущенные ночи не влияют на результат
        assertEquals(UserClassification.LARK, classify(
                "01.10.25 21:00;02.10.25 06:00;GOOD",
                "10.10.25 21:30;11.10.25 06:30;GOOD"
        ));
    }

    @Test
    void shouldReturnUndefinedForEmptyLog() {
        assertEquals(UserClassification.UNDEFINED, classify());
    }

    @Test
    void shouldReturnUndefinedWhenNoNightSessions() {
        assertEquals(UserClassification.UNDEFINED, classify("01.10.25 14:00;01.10.25 15:00;GOOD"));
    }


    @Test
    void shouldClassifyNightAsOwlWhenBedtimeAfterMidnight() {
        assertEquals(UserClassification.OWL, classify("02.10.25 01:00;02.10.25 10:00;GOOD"));
    }

    @Test
    void shouldClassifyNightAsPigeonOnOwlBoundaries() {
        // засыпание ровно в 23:00 и пробуждение ровно в 9:00 - не "после"
        assertEquals(UserClassification.PIGEON, classify("01.10.25 23:00;02.10.25 09:00;GOOD"));
    }

    @Test
    void shouldClassifyNightAsPigeonOnLarkBoundaries() {
        // засыпание ровно в 22:00 и пробуждение ровно в 7:00 - не "до"
        assertEquals(UserClassification.PIGEON, classify("01.10.25 22:00;02.10.25 07:00;GOOD"));
    }
}
