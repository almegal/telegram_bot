package pro.sky.telegrambot.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import pro.sky.telegrambot.exception.DateTimeBeforeException;
import pro.sky.telegrambot.exception.ErrorMessage;
import pro.sky.telegrambot.exception.MessageParseException;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * Сценарии для теста:
 * - Что будет если инпут корректный?
 * - Что будет если инпут не некорректный?
 * - Что будет если дата или время раньше чем сейчас?
 * */
public class MessageParserComponentUnitTest {

    private final MessageParserComponent parser = new MessageParserComponent();

    private final LocalDateTime NOTIFICATION_VALIDATE_DATE_TIME = LocalDateTime.of(2030, 12, 12, 15, 0);
    private final String NOTIFICATION_TASK = "Сделать что-то важное";

    @Test
    @DisplayName("Корректное создание задачи с валидным инпутом")
    public void createNotificationTaskWithValidInput() {
        // подготовка данных
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String expectedDateTime = formatter.format(NOTIFICATION_VALIDATE_DATE_TIME);
        String message = expectedDateTime + " " + NOTIFICATION_TASK;
        // подготовка ожидаемого значения
        NotificationTask expected = new NotificationTask(1,
                NOTIFICATION_TASK,
                NOTIFICATION_VALIDATE_DATE_TIME);
        // подготовка актуального значения
        NotificationTask actual = parser.parseToNotificationTask(1, message);
        // тест
        assertEquals(expected.getTask(), actual.getTask());
        assertEquals(expected.getNotificationDateTime(), actual.getNotificationDateTime());
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "Сделать что-то важное к 15.05.2025 15:00",
            "15.04.25 15:00 Позвонить в прачечную",
            "02:02:2028 15:00 Сделать что-то",
            "02.02.2029 15.00 Сделать что-то важнее важного",
            "02 02 2029 15:00 Сделать что-то важнее важного",
            "02/02/2029 15:00 Сделать что-то важнее важного",
            "02.02.2029 5:00 Сделать что-то важнее важного",
            "", " "
    })
    @DisplayName("Исключение если формат данных не валидный")
    public void throwMessageParseExceptionIfInputInvalid(String message) {
        // подготовка актуального значения
        MessageParseException thrown = assertThrows(MessageParseException.class,
                () -> {
                    parser.parseToNotificationTask(1, message);
                });
        // подготовка ожидаемого значения
        String expected = String.format(
                ErrorMessage.PARSE_EXCEPTION_MESSAGE.getMessage(),
                message
        );
        assertEquals(expected, thrown.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "31.02.2025 25:00  Добавить один час в сутки",
            "32.10.2030 00:00 Добавить один день в месяц",
            "31.10.2030 00:65 Добавить один день в месяц"
    })
    @DisplayName("Исключение если дата и время не существуюе")
    public void throwMessageParseExceptionIfDataTimeIsNotExist(String message) {
        // подготовка актуального значения
        MessageParseException thrown = assertThrows(MessageParseException.class,
                () -> {
                    parser.parseToNotificationTask(1, message);
                });
        // подготовка ожидаемого значения
        String expected = ErrorMessage.INVALID_DATA_TIME_PARSE_EXCEPTION_MESSAGE.getMessage();
        assertEquals(expected, thrown.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "10.02.2023 15:00 Some text;2023-02-10T15:00",
            "16.05.2024 23:50 Some text;2024-05-16T23:50",
            "17.06.2024 06:00 some text;2024-06-17T06:00"
    }, delimiter = ';')
    @DisplayName("Исключение если дата и время раньше текущего времени")
    public void throwIfDateTimeIsBefore(String message, String dateTime) {
        // подготовка актуального значения
        DateTimeBeforeException thrown = assertThrows(DateTimeBeforeException.class,
                () -> {
                    parser.parseToNotificationTask(1, message);
                });
        // подготовка ожидаемого значения

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        String expected = String.format(
                ErrorMessage.BEFORE_DATA_TIME_EXCEPTION_MESSAGE.getMessage(),
                now,
                dateTime
        );
        assertEquals(expected, thrown.getMessage());
    }
}

