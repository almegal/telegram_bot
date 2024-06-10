package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.HandlerUpdateService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    @Autowired
    private TelegramBot telegramBot;
    private HandlerUpdateService handlerUpdateService;

    public TelegramBotUpdatesListener(HandlerUpdateService handlerUpdateService) {
        this.handlerUpdateService = handlerUpdateService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            String msg = update.message().text();
            boolean isCommand = msg.split(" ")[0].startsWith("/");
            SendMessage sendMessage;
            if (isCommand) {
                sendMessage = handlerUpdateService.handleUpdateCommand(update);
                telegramBot.execute(sendMessage);
            } else {
                sendMessage = handlerUpdateService.handleUpdateMsg(update, null);
                telegramBot.execute(sendMessage);
            }
//            telegramBot.execute(sendMessage);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
