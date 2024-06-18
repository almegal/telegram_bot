package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.component.MessageParserComponent;
import pro.sky.telegrambot.exception.CommandNotFoundException;
import pro.sky.telegrambot.model.NotificationTask;

/* Класс для обработки входящих обращений
 * Методы handlUpdateMsg и HandlerUpdateCommand вместо параметра Update принимают
 * строку и айди для слабой связанности и простоты тестирования */
@Service
public class HandlerMessageService {
    private final NotificationTaskService service;
    private final MessageParserComponent messageParserComponent;

    // инъекция бинов
    public HandlerMessageService(NotificationTaskService service,
                                 MessageParserComponent messageParserComponent) {
        this.service = service;
        this.messageParserComponent = messageParserComponent;
    }

    // метод который обрабатывает сообщение боту
    public SendMessage handleUpdateMessage(long chatId, String message) {
        // парсим входящее сообщение и получаем задачу
        NotificationTask task = messageParserComponent.parseToNotificationTask(chatId, message);
        // сохраняем в бд
        service.saveTask(task);
        // информируем пользоватея что все прошло успешно
        return new SendMessage(chatId, "Ваше напоминание сохранено: " + message);
    }

    // метод для обработки команд
    public SendMessage handleUpdateCommand(long chatId, String message) {
        // если комманда известна то передаем соответсвующие сообщение на команду
        if (message.equalsIgnoreCase("/start")) {
            return new SendMessage(chatId, "Приветствую");
        // иначе выбросим исключение
        } else {
            throw new CommandNotFoundException("Неизвестная команда");
        }
    }
}
