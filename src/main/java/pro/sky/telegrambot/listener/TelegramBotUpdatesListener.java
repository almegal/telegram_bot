package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.HandlerMessageService;

import javax.annotation.PostConstruct;
import java.util.List;

// Сервис принимающиц апдейты от телеграмм апи
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    // инъектция сервиса обработки сообщений
    private final HandlerMessageService handlerMessageService;

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(HandlerMessageService handlerMessageService) {
        this.handlerMessageService = handlerMessageService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    // реализуем метод обработки входящих сообщений
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            // получаем id и содержание сообщения
            long chatId = update.message().chat().id();
            String msg = update.message().text();
            // проверяем с чего начинается строка
            boolean isCommand = msg.split(" ")[0].startsWith("/");
            SendMessage sendMessage;
            if (isCommand) {
                // если команда обрабатываем как команду
                sendMessage = handlerMessageService.handleUpdateCommand(chatId, msg);
            } else {
                // иначе обрабатываем сообщение
                sendMessage = handlerMessageService.handleUpdateMessage(chatId, msg);
            }
            // если аспект не выдал ошибок
            // значит sendMessage не равен нуля
            // в противном случае все ошибки отработаны, а пользователь проинформирован
            if (sendMessage != null) {
                telegramBot.execute(sendMessage);
            }
        });
        // подверждаем что все апдейты обработаны
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
