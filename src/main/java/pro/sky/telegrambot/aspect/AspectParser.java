package pro.sky.telegrambot.aspect;

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

/*
 * Аспект преобразует строку в задачу для напоминаяния
 * */
@Aspect
@Component
public class AspectParser {

    private final Logger logger = LoggerFactory.getLogger(AspectParser.class);

    @Around("execution(* pro.sky.telegrambot.service.HandlerUpdateService.handleUpdateMsg(..))")
    public Object parserMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        // получаем идентификатор чата и сообщение
        long chatId = (long) joinPoint.getArgs()[0];
        String msg = (String) joinPoint.getArgs()[1];
        // Регулярное выражение для разбора строки
        String regex = "(\\d{2}\\.\\d{2}\\.\\d{4})\\s+(\\d{2}:\\d{2})\\s+(.+)";
        // Создание шаблона и сопоставителя
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(msg);
        // если не совпадает шаблону
        // логируем, передаем управление и выбрасываем ошибку
        if (!matcher.matches()) {
            IllegalArgumentException ex = new IllegalArgumentException("Передан некорректный формат: " + msg + "; " +
                    "Ожидается: dd.mm.yyyy hh:mm text");
//            logger.error(ex.getMessage());
            // возращаем управление
            joinPoint.proceed();
            throw ex;
        }
        // парсим дату, время и напоминание
        LocalDate localDate = parseDate(matcher.group(1));
        LocalTime localTime = parseTime(matcher.group(2));
        String task = matcher.group(3);
        // пробуем создать объект
        try {
            NotificationTask notificationTask = new NotificationTask(chatId, task, localTime, localDate);
            // создаем новый список аргументов и передаем дальше управление
            Object[] newArgs = {chatId, msg, notificationTask};
            return joinPoint.proceed(newArgs);
        } catch (IllegalArgumentException ex) {
//            logger.error(ex.getMessage());
            joinPoint.proceed();
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    // метод для получаения даты
    private LocalDate parseDate(String dateString) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(dateString, dateFormatter);
    }

    // метод для получаения времени
    private LocalTime parseTime(String timeString) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeString, timeFormatter);
    }
}
