package pro.sky.telegrambot.component;

import org.springframework.stereotype.Component;
import pro.sky.telegrambot.exception.ErrorMessage;
import pro.sky.telegrambot.exception.MessageParseException;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*Компонент для парсинга сообщения строки из формата
 *   dd.mm.yyyy hh:mm notificationText
 * в объект типа NotificationTask
 * */
@Component
public class MessageParserComponent {

    public NotificationTask parseToNotificationTask(long chatId, String message) throws IllegalArgumentException {
        // Регулярное выражение для разбора строки
        String regex = "(\\d{2}\\.\\d{2}\\.\\d{4})\\s+(\\d{2}:\\d{2})\\s+(.+)";

        // Создание шаблона и сопоставителя
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        // если не совпадает шаблону
        // логируем, передаем управление и выбрасываем ошибку
        if (!matcher.matches()) {
            // подготовка сообщения об ошибки
            String errorMessage = String.format(
                    ErrorMessage.PARSE_EXCEPTION_MESSAGE.getMessage(),
                    message
            );
            // выбрасывем исключение
            throw new MessageParseException(errorMessage);
        }

        // парсим дату, время и напоминание
        String dateString = matcher.group(1);
        String timeString = matcher.group(2);
        String task = matcher.group(3);

        try {
            // получаем дату и время
            LocalDateTime localDateTime = convertStringToLocalDateTime(dateString, timeString);
            // вернуть значение
            return new NotificationTask(chatId, task, localDateTime);
        } catch (DateTimeParseException ex) {
            throw new MessageParseException(ErrorMessage.INVALID_DATA_TIME_PARSE_EXCEPTION_MESSAGE.getMessage());
        }
    }

    // метод для получаения даты
    private LocalDateTime convertStringToLocalDateTime(String dateString, String timeString) throws DateTimeParseException {
        // Шаблон для даты
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(dateString, dateFormatter);
        // Шаблон для времени
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.parse(timeString, timeFormatter);
        // возращаем дату и время
        return LocalDateTime.of(localDate, localTime);
    }
}
