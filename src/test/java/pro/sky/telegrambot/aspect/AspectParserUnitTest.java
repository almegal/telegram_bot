package pro.sky.telegrambot.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AspectParserUnitTest {

    private final long VALID_ID = 12345L;
    private final String VALID_MESSAGE = "12.06.2025 14:00 Напоминание сделать до 13:00 что нибудь важное";

    private AspectParser aspectParser;
    private ProceedingJoinPoint proceedingJoinPoint;

    @BeforeEach
    public void setUp() {
        aspectParser = new AspectParser();
        proceedingJoinPoint = mock(ProceedingJoinPoint.class);
    }

    /*Тест проверяет что при валидном сообщение
     * передается управление с правильными параметрами
     * корректно парситься строка в таск*/
    @Test
    @DisplayName("Тестируем аспект с валидным сообщением")
    public void testValidMessageParsing() throws Throwable {
        // Настройка тестовых данных

        Object[] args = {VALID_ID, VALID_MESSAGE};

        // Настройка mock поведения ProceedingJoinPoint
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
        when(proceedingJoinPoint.proceed(any(Object[].class))).thenReturn("Success");

        // Выполнение теста
        Object result = aspectParser.parserMessage(proceedingJoinPoint);

        // Проверка результата
        assertEquals("Success", result);

        // Проверка аргументов, переданных в proceed
        ArgumentCaptor<Object[]> argumentCaptor = ArgumentCaptor.forClass(Object[].class);
        // Проверка что вызова метода для передачи управления
        verify(proceedingJoinPoint, times(1)).proceed(argumentCaptor.capture());

        //  Получаем новые аргументы из аспекта
        Object[] capturedArgs = argumentCaptor.getValue();
        // Проверям что вернулось 3 аргумента и один из них NotificationTask
        assertEquals(3, capturedArgs.length);
        assertTrue(capturedArgs[2] instanceof NotificationTask);

        // приведение типов для тестирования возращенного таска
        NotificationTask task = (NotificationTask) capturedArgs[2];
        assertEquals(VALID_ID, task.getChatId());
        assertEquals("Напоминание сделать до 13:00 что нибудь важное", task.getTask());
        assertEquals(LocalDate.of(2025, 6, 12), task.getNotificationDate());
        assertEquals(LocalTime.of(14, 0), task.getNotificationTime());
    }

    /* Тест проверяет что при некорректном формате сообщения
     * парсер выбрасыает исключение illegalArgumentException
     * */
    @ParameterizedTest
    @ValueSource(strings = {"Абракадабра",
            "20:12:2025 14:00 simsalabim",
            "20.12.25 15:00 Нужно в магазин до 12:00",
            "20.12.2025 15.00 Выключить печь",
    })
    @DisplayName("Тестируем аспект с некорректным входным сообщением")
    public void testInvalidMessageParsing(String invalidMessage) throws Throwable {
        Object[] args = {VALID_ID, invalidMessage};

        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        // Проверка выброса исключения
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aspectParser.parserMessage(proceedingJoinPoint)
        );

        assertEquals("Передан некорректный формат: " + invalidMessage + "; Ожидается: dd.mm.yyyy hh:mm text",
                exception.getMessage());

        // Убедитесь, что метод proceed был вызван для возврата управления
        verify(proceedingJoinPoint, times(1)).proceed();
    }
    /* Тест проверяет что при корректном формате и не правильным датой/временем
     * при попытке создать таск выбрасывает исключение
     * */
    @ParameterizedTest
    @ValueSource(strings = {"12.02.2024 15:00 Что-то не важное",
            "11.06.2024 07:13 Напоминание"})
    public void testWhenDateOrTimeLessThanNow(String invalidMessage) {
        // подготовка входных данных
        Object[] args = {VALID_ID, invalidMessage};
        // настройка поведения мока
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
        // проверка выброса исключения
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aspectParser.parserMessage(proceedingJoinPoint)
        );
        assertEquals("Дата и время не может быть раньше текущей", exception.getMessage());
    }
}
