package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.HandlerMessageService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
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
                sendMessage = handlerMessageService.handleUpdateMsg(chatId, msg);
            }
            // отправляем обратно
            telegramBot.execute(sendMessage);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
