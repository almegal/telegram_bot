package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

@Service
public class HandlerUpdateService {
    private final NotificationTaskRepository repository;

    public HandlerUpdateService(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    // вызов аспекта здесь
    public SendMessage handleUpdateMsg(Update update, NotificationTask task) throws IllegalArgumentException {
        long chatID = update.message().chat().id();
        String msg = update.message().text();
        // сохраняем в бд
        saveTask(task);
        // информируем пользоватея что все прошло успешно
        return new SendMessage(chatID, "Ваше напоминание сохранено: " + msg);
    }

    public SendMessage handleUpdateCommand(Update update) {
        long chatID = update.message().chat().id();
        return new SendMessage(chatID, "welcome");
    }

    public void saveTask(NotificationTask task) {
        repository.save(task);
    }
}
