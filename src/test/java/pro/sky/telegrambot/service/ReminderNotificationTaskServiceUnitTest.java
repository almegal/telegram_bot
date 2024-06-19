package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/* */
@ExtendWith(MockitoExtension.class)
public class ReminderNotificationTaskServiceUnitTest {
    private final List<NotificationTask> MOCK_LIST_TASK = new ArrayList<>(
            List.of(new NotificationTask(1L, "some task #1", LocalDateTime.now()),
                    new NotificationTask(2L, "some task #2", LocalDateTime.now()),
                    new NotificationTask(3L, "some task #3", LocalDateTime.now()))
    );
    private final List<NotificationTask> MOCK_EMPTY_LIST = Collections.emptyList();

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
