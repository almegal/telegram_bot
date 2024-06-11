package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationReminderService {
    final private TelegramBot bot;
    final private NotificationTaskRepository repository;

    public NotificationReminderService(TelegramBot bot, NotificationTaskRepository repository) {
        this.bot = bot;
        this.repository = repository;
    }
    // запуск метода каждую минуту
    @Scheduled(cron = "0 0/1 * * * *")
    public void getNotificationTasksWhereIsTime() {
        // получить текущее дату и врем до минут
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDate localDate = localDateTime.toLocalDate();
        LocalTime localTime = localDateTime.toLocalTime();
        // получаем список нотификаций
        List<NotificationTask> tasks = repository.getByDateAndTimeNow(localDate, localTime);
        // если не пустой, то отправить нотификацию
        if (!tasks.isEmpty()) {
            sendNotificationTask(tasks);
        }
    }

    public void sendNotificationTask(List<NotificationTask> tasks) {
        // для каждой нотификации в списке
        // параллельно отправляем сообщением в чат
        tasks.stream()
                .parallel()
                .forEach(t -> {
                    SendMessage sendMessage = new SendMessage(t.getChatId(), "Напоминаю:\n" + t.getTask());
                    bot.execute(sendMessage);
                });
    }
}
