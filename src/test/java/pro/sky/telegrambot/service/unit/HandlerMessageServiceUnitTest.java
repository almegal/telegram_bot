package pro.sky.telegrambot.service.unit;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.component.MessageParserComponent;
import pro.sky.telegrambot.exception.CommandNotFoundException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.HandlerMessageService;
import pro.sky.telegrambot.service.NotificationTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/* Сценарии для тестирования
 * - Поведение при валидных данных
 * - Поведение не валидных данных
 * */
@ExtendWith(MockitoExtension.class)
public class HandlerMessageServiceUnitTest {
    private final String VALID_MESSAGE = "14.02.2025 15:00 Some task";
    private final String INVALID_MESSAGE = "14.02.2025 task";

    @Mock
    private NotificationTaskService service;
    @Spy
    private MessageParserComponent parser;
    @InjectMocks
    private HandlerMessageService handlerMessageService;

    @Test
    @DisplayName("Возращает корректное значение и корректно вызывает методы зависимостей")
    public void handlerMessageIfInputIsCorrect() {
        // подготовка поведения мока
        doNothing().when(service).saveTask(any(NotificationTask.class));
        // получения актуального значения
        SendMessage actual = handlerMessageService.handleUpdateMessage(1L, VALID_MESSAGE);
        // подготовка ожидаемого значения
        SendMessage expected = new SendMessage(1L, "Ваше напоминание сохранено: " + VALID_MESSAGE);
        // проверка
        assertEquals(
                expected.getParameters().get("chat_id"),
                actual.getParameters().get("chat_id"));
        assertEquals(
                expected.getParameters().get("text"),
                actual.getParameters().get("text")
        );
        verify(service, times(1)).saveTask(any(NotificationTask.class));
        verify(parser, times(1)).parseToNotificationTask(1L, VALID_MESSAGE);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "15.10.2025 15.00 some task",
            "15/10/2025 15:00 some task",
            "15:10:2025 15:00 some task",
            ""
    })
    @DisplayName("Не вызывает метод service.save при возникновение ошибки при парсинге")
    public void notInvokeMethodSaveIfParsingThrowException(String message) {
        // ожидаем выброс
        Throwable throwable = assertThrows(Throwable.class,
                () -> parser.parseToNotificationTask(anyLong(), message));
        // проверка вызываемых методов
        verify(service, never()).saveTask(any(NotificationTask.class));
    }

    @Test
    @DisplayName("Возращает на команду /start корректный объект")
    public void whenGotStartCommandWillReturnCorrectSendMessage() {
        // подготовка актуального результата
        SendMessage actual = handlerMessageService.handleUpdateCommand(1L, "/start");
        // подготовка ожидааемого значения
        SendMessage expected = new SendMessage(1L, "Приветствую");
        // тест
        assertEquals(
                expected.getParameters().get("chat_id"),
                actual.getParameters().get("chat_id"));
        assertEquals(
                expected.getParameters().get("text"),
                actual.getParameters().get("text")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/starts",
            "/sta",
            "/starting",
            "/",
            "start/start",
            "/active"
    })
    @DisplayName("Если передана неизвестная команда - выбросить исключения")
    public void whenGivenUnknownCommandReturnException(String command) {
        // ожидаем выброс
        CommandNotFoundException thrown = assertThrows(CommandNotFoundException.class,
                () -> handlerMessageService.handleUpdateCommand(1L, command));
        //test
        assertEquals("Неизвестная команда", thrown.getMessage());
    }
}
