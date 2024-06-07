package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HandlerUpdateService {
    @Autowired
    private TelegramBot bot;


    public void handleUpdate(Update update) {
        long chatID = update.message().chat().id();
        String msg = update.message().text();

        boolean isCommand = msg.split(" ")[0].startsWith("/");
        if (isCommand) {
            SendMessage request = new SendMessage(chatID, "welcome");
            bot.execute(request);
        }
    }
}
