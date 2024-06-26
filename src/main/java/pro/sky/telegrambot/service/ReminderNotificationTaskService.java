package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/*Сервис для нотификации задач пользователя
 * Реализован через механизм sheduled спринга
 * */
@Service
public class ReminderNotificationTaskService {
    final private TelegramBot bot;
    final private NotificationTaskService service;

    // инъекция бинов
    public ReminderNotificationTaskService(TelegramBot bot,
                                           NotificationTaskService service) {
        this.bot = bot;
        this.service = service;
    }

    // запуск метода каждую минуту
    @Scheduled(cron = "0 0/1 * * * *")
    public void getNotificationTasksWhereIsTime() {
        // получить текущее дату и врем до минут
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        // получаем список нотификаций
        List<NotificationTask> tasks = service.getByDateAndTimeNow(localDateTime);
        // если не пустой, то отправить нотификацию
        if (!tasks.isEmpty()) {
            sendNotificationTask(tasks);
        }
    }

    // метод для отправки напоминаний
    public void sendNotificationTask(List<NotificationTask> tasks) {
        // для каждой нотификации в списке
        // параллельно отправляем сообщением в чат
        // и деактивируем задачу
        tasks.stream()
                .parallel()
                .forEach(t -> {
                    SendMessage sendMessage = new SendMessage(t.getChatId(), "Напоминаю:\n" + t.getTask());
                    bot.execute(sendMessage);
                });
    }
}
