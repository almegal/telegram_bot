package pro.sky.telegrambot.exception;

public class MessageParseException extends IllegalArgumentException {
    public MessageParseException(String s) {
        super(s);
    }
}
