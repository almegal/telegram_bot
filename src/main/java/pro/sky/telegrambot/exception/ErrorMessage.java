package pro.sky.telegrambot.exception;

public enum ErrorMessage {
    PARSE_EXCEPTION_MESSAGE("Передан некорректный формат: %s;\n" +
            "Ожидается: dd.mm.yyyy hh:mm text"),
    INVALID_DATA_TIME_PARSE_EXCEPTION_MESSAGE("Введенное значение для даты или времени не существует." +
            " Проверьте правильность введенных данных"),
    BEFORE_DATA_TIME_EXCEPTION_MESSAGE("Дата и время не может быть раньше текущей." +
            "\nСейчас: %s" +
            "\nПередано: %s");
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
