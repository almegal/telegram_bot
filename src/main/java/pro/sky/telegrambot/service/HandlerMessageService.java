package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.component.MessageParserComponent;
import pro.sky.telegrambot.model.NotificationTask;

/* Класс для обработки входящих обращений
 * Методы handlUpdateMsg и HandlerUpdateCommand вместо параметра Update принимают
 * строку и айди для слабой связанности и простоты тестирования */
@Service
public class HandlerMessageService {
    private final NotificationTaskService service;
    private final MessageParserComponent messageParserComponent;

    public HandlerMessageService(NotificationTaskService service,
                                 MessageParserComponent messageParserComponent) {
        this.service = service;
        this.messageParserComponent = messageParserComponent;
    }

    public SendMessage handleUpdateMsg(long chatId, String message) {
        // парсим входящее сообщение и получаем задачу
        try {
            NotificationTask task = messageParserComponent.parseToNotificationTask(chatId, message);
            service.saveTask(task);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // сохраняем в бд
        // информируем пользоватея что все прошло успешно
        return new SendMessage(chatId, "Ваше напоминание сохранено: " + message);
    }

    public SendMessage handleUpdateCommand(long chatId, String message) {
        if (message.startsWith("/start")) {
            return new SendMessage(chatId, "Приветсвтую");
        } else {
            return new SendMessage(chatId, "Неизвестная команда");
        }
    }
}
