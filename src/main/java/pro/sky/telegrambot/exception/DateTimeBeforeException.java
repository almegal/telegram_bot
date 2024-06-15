package pro.sky.telegrambot.exception;

public class DateTimeBeforeException extends IllegalArgumentException {
    public DateTimeBeforeException(String s) {
        super(s);
    }
}
