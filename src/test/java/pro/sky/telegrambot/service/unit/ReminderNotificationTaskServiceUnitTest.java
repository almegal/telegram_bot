package pro.sky.telegrambot.service.unit;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.ConfigurationTest;
import pro.sky.telegrambot.service.NotificationTaskService;
import pro.sky.telegrambot.service.ReminderNotificationTaskService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pro.sky.telegrambot.ConfigurationTest.*;

/* */
@ExtendWith(MockitoExtension.class)
public class ReminderNotificationTaskServiceUnitTest {

    @Mock
    private TelegramBot bot;
    @Mock
    private NotificationTaskService service;
    @InjectMocks
    private ReminderNotificationTaskService reminderNotificationTaskService;

    @Test
    @DisplayName("Корректно отрабатывает метод с пустым списком, не вызывает метод sendNotificationTask")
    public void whenTimeIsInvokeReminderGetNotificationTaskList() {
        // настройка поведения
        when(service.getByDateAndTimeNow(any(LocalDateTime.class)))
                .thenReturn(MOCK_EMPTY_LIST);
        // вызов тестирумого метода
        reminderNotificationTaskService.getNotificationTasksWhereIsTime();
        // проверяем работу метода
        verify(service, times(1)).getByDateAndTimeNow(any(LocalDateTime.class));
        verify(bot, never()).execute(any(SendMessage.class));
    }

    @Test
    @DisplayName("Корректно отрабатывает метод с не пусты списком, и вызывает метод sendNotificationTask")
    public void whenInvokeSendNotificationShouldInvokeBotExcute() {
        // настройка поведения
        when(service.getByDateAndTimeNow(any(LocalDateTime.class))).thenReturn(MOCK_LIST_TASK);
        when(bot.execute(any(SendMessage.class))).thenReturn(any(SendResponse.class));
        // вызов тестируемого метода
        reminderNotificationTaskService.getNotificationTasksWhereIsTime();
        // проверяем поведение
        verify(service, times(1)).getByDateAndTimeNow(any(LocalDateTime.class));
        verify(bot, times(MOCK_LIST_TASK.size())).execute(any(SendMessage.class));
    }
}
