package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.util.Optional;

@Service
public class HandlerUpdateService {
    private final NotificationTaskRepository repository;
    @Autowired
    private TelegramBot bot;

    public HandlerUpdateService(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    // вызов аспекта здесь
    public void handleUpdateMsg(Update update, NotificationTask task) throws IllegalArgumentException {
        long chatID = update.message().chat().id();
        String msg = update.message().text();
        // создаем опшионал из переданного аргумента
        Optional<NotificationTask> optionalNotificationTask = Optional.ofNullable(task);
        // если значения нет, то уведомляем пользователя о некорректном формате
        // и выбрасываем исключение
//        task = optionalNotificationTask.orElseThrow(() -> {
//            SendMessage request = new SendMessage(chatID, "Ваше напоминание не сохранено" +
//                    "\nВы передали: " + msg +
//                    "\nОжидаемый формат: " + "dd.mm.yyyy hh:mm text");
//            bot.execute(request);
//            return new IllegalArgumentException("Ooops");
//        });
        // сохраняем в бд
        saveTask(task);
        // информируем пользоватея что все прошло успешно
        SendMessage request = new SendMessage(chatID, "Ваше напоминание сохранено: " + msg);
        bot.execute(request);
    }

    public void handleUpdateCommand(Update update) {
        long chatID = update.message().chat().id();
        SendMessage request = new SendMessage(chatID, "welcome");
        bot.execute(request);
    }

    public void saveTask(NotificationTask task) {
        repository.save(task);
    }
}
