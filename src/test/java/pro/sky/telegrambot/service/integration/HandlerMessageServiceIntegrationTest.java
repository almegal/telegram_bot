package pro.sky.telegrambot.service.integration;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import pro.sky.telegrambot.component.MessageParserComponent;
import pro.sky.telegrambot.exception.DateTimeBeforeException;
import pro.sky.telegrambot.exception.MessageParseException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.ErrorHandlerService;
import pro.sky.telegrambot.service.HandlerMessageService;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static pro.sky.telegrambot.ConfigurationTest.MOCK_MESSAGE_FROM_USER;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HandlerMessageServiceIntegrationTest {
    @SpyBean
    static private ErrorHandlerService errorHandlerService;
    @SpyBean
    private NotificationTaskService service;
    @SpyBean
    private MessageParserComponent parser;
    @Autowired
    private HandlerMessageService handlerMessage;

    // метод возращающий аргументы: экзампляр ошибки, тест для проверки обработки, message
    static Stream<Arguments> argProvider() {
        return Stream.of(
                Arguments.of(
                        (Runnable) () -> {
                            verify(errorHandlerService, times(1))
                                    .handleDateTimeBeforeException(any(DateTimeBeforeException.class), anyLong());
                        },
                        "15.10.1990 15:00 %)"),
                Arguments.of(
                        (Runnable) () -> {
                            verify(errorHandlerService, times(1))
                                    .handlerMessageParseException(any(MessageParseException.class), anyLong());
                        }, ""),
                Arguments.of(
                        (Runnable) () -> {
                            verify(errorHandlerService, times(1))
                                    .handlerThrowableException(any(Throwable.class), anyLong());
                        }, null)
        );
    }

    @Test
    @DisplayName("Обработка сообщения с задачей, интеграция и вызов зависимостей")
    public void shouldReturnValidSendResponseWhenMessageHandlerInvoke() {
        // подготовка ожидаемого результата
        SendMessage expected = new SendMessage(
                1L, "Ваше напоминание сохранено: "
                + MOCK_MESSAGE_FROM_USER);
        // подгтовка актуального результата
        SendMessage actual = handlerMessage.handleUpdateMessage(1L, MOCK_MESSAGE_FROM_USER);
        // тестирование
        verify(service, times(1)).saveTask(any(NotificationTask.class));
        verify(parser, times(1)).parseToNotificationTask(1L, MOCK_MESSAGE_FROM_USER);
        assertEquals(expected.getParameters(), actual.getParameters());
    }

    @ParameterizedTest
    @MethodSource("argProvider")
    @DisplayName("При возникновение ошибки вызывается соответвующий метод обработки")
    public void shouldReturnNullWhenExceptionOccur(Runnable tester, String message) {
        // подготовка актуального значения
        SendMessage actual = handlerMessage.handleUpdateMessage(1L, message);
        // тест
        tester.run();
        assertNull(actual);
    }
}
