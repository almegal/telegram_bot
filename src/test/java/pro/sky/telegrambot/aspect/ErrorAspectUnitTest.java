package pro.sky.telegrambot.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.exception.CommandNotFoundException;
import pro.sky.telegrambot.exception.DateTimeBeforeException;
import pro.sky.telegrambot.exception.MessageParseException;
import pro.sky.telegrambot.service.ErrorHandlerService;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;


// проверяем что обрабытываются исключение соответсвующими методами
@ExtendWith(MockitoExtension.class)
public class ErrorAspectUnitTest {

    @Mock
    static private ErrorHandlerService errorHandlerService;
    @Mock
    private ProceedingJoinPoint joinPointMock;
    @InjectMocks
    private ErrorAspect errorAspect;

    // метод возращающий аргументы: экзампляр ошибки, тест для проверки обработки
    static Stream<Arguments> argProvider() {
        return Stream.of(
                Arguments.of(
                        new DateTimeBeforeException(""),
                        (Consumer<DateTimeBeforeException>) thrown -> {
                            verify(errorHandlerService, times(1))
                                    .handleDateTimeBeforeException(thrown, 1L);
                        }),
                Arguments.of(
                        new MessageParseException(""),
                        (Consumer<MessageParseException>) thrown -> {
                            verify(errorHandlerService, times(1))
                                    .handlerMessageParseException(thrown, 1L);
                        }),
                Arguments.of(
                        new CommandNotFoundException(""),
                        (Consumer<CommandNotFoundException>) thrown -> {
                            verify(errorHandlerService, times(1))
                                    .handlerCommandNotFoundException(thrown, 1L);
                        }),
                Arguments.of(
                        new Throwable(""),
                        (Consumer<Throwable>) thrown -> {
                            verify(errorHandlerService, times(1))
                                    .handlerThrowableException(thrown, 1L);
                        })
        );
    }

    @ParameterizedTest
    @MethodSource("argProvider")
    @DisplayName("Для каждого типа исключений вызывется соответсвующий метод для обработки ошибок")
    public void invoker(Throwable thrown, Consumer<Throwable> tester) throws Throwable {
        // настройка поведения
        when(joinPointMock.getArgs()).thenReturn(new Object[]{1L});
        when(joinPointMock.proceed()).thenThrow(thrown);
        // вызываем тестируемого метода
        errorAspect.handleError(joinPointMock);
        // проверка результата
        tester.accept(thrown);
    }

}
