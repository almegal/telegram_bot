package pro.sky.telegrambot.aspect;

import com.pengrad.telegrambot.model.Update;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
public class AspectParser {

    private Logger logger = LoggerFactory.getLogger(AspectParser.class);

    @Around("execution(* pro.sky.telegrambot.service.HandlerUpdateService.handleUpdateMsg(..))")
    public void parserMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        Update update = (Update) joinPoint.getArgs()[0];
        // получаем идентификатор чата и сообщение
        long chatID = update.message().chat().id();
        String message = update.message().text();
        // Регулярное выражение для разбора строки
        String regex = "(\\d{2}\\.\\d{2}\\.\\d{4})\\s+(\\d{2}:\\d{2})\\s+(.+)";
        // Создание шаблона и сопоставителя
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        // если не совпадает шаблону
        // логируем, передаем управление и выбрасываем ошибку
        if (!matcher.matches()) {
            IllegalArgumentException ex = new IllegalArgumentException("Некооректные данные: " + message);
            logger.error(ex.getMessage());
            joinPoint.proceed();
            throw ex;
        }
        // парсим дату, время и напоминание
        LocalDate localDate = parseDate(matcher.group(1));
        LocalTime localTime = parseTime(matcher.group(2));
        String task = matcher.group(3);
        // пробуем создать объект
        // если ошибка: логируем, передаем управление и выбрасываем исключение
        NotificationTask notificationTask = new NotificationTask(chatID, task, localTime, localDate);
        // создаем новый список аргументов и передаем дальше управление
        Object[] newArgs = {update, notificationTask};
        joinPoint.proceed(newArgs);
    }

    private LocalDate parseDate(String dateString) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(dateString, dateFormatter);
    }

    private LocalTime parseTime(String timeString) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeString, timeFormatter);
    }
}
