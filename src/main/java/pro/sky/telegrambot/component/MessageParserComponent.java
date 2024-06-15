package pro.sky.telegrambot.component;

import org.springframework.stereotype.Component;
import pro.sky.telegrambot.model.NotificationTask;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MessageParserComponent {

    public NotificationTask parseToNotificationTask(long chatId, String message) throws ParseException {
        // Регулярное выражение для разбора строки
        String regex = "(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2})\\s+(.+)";

        // Создание шаблона и сопоставителя
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        // если не совпадает шаблону
        // логируем, передаем управление и выбрасываем ошибку
        if (!matcher.matches()) {
            IllegalArgumentException ex = new IllegalArgumentException("Передан некорректный формат: " + message + "; " +
                    "Ожидается: dd.mm.yyyy hh:mm text");
            // возращаем управление
            throw ex;
        }

        // парсим дату, время и напоминание
        LocalDateTime localDateTime = convertStringToLocalDateTime(matcher.group(1));
        String task = matcher.group(2);
        // вернуть значение
        return new NotificationTask(chatId, task, localDateTime);
    }

    // метод для получаения даты
    private LocalDateTime convertStringToLocalDateTime(String dateTimeString) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm");
        return LocalDateTime.parse(dateTimeString, dateFormatter);
    }
}
