package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

/* Класс для обработки входящих обращений
 * Методы handlUpdateMsg и HandlerUpdateCommand вместо параметра Update принимают
 * строку и айди для слабой связанности и простоты тестирования */
@Service
public class HandlerUpdateService {
    private final NotificationTaskRepository repository;

    public HandlerUpdateService(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    /* Вызов аспекта здесь AspectParser
     * Перехватывает входящие аргументы и парсит стркоу
     *  */
    public SendMessage handleUpdateMsg(long chatId, String msg, NotificationTask task) throws IllegalArgumentException {
        // сохраняем в бд
        saveTask(task);
        // информируем пользоватея что все прошло успешно
        return new SendMessage(chatId, "Ваше напоминание сохранено: " + msg);
    }

    public SendMessage handleUpdateCommand(long chatId, String msg) {
        if (msg.startsWith("/start")) {
            return new SendMessage(chatId, "Приветсвтую");
        } else {
            return new SendMessage(chatId, "Неизвестная команда");
        }
    }

    public void saveTask(NotificationTask task) {
        repository.save(task);
    }
}
