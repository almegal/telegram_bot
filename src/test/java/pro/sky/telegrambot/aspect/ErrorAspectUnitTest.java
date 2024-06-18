package pro.sky.telegrambot.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.exception.CommandNotFoundException;
import pro.sky.telegrambot.exception.ErrorMessage;
import pro.sky.telegrambot.exception.MessageParseException;
import pro.sky.telegrambot.service.ErrorHandlerService;

import static org.mockito.Mockito.*;


// проверяем что обрабытываются исключение соответсвующими методами
@ExtendWith(MockitoExtension.class)
public class ErrorAspectUnitTest {

    @Mock
    private ProceedingJoinPoint joinPointMock;
    @Mock
    private ErrorHandlerService errorHandlerService;
    @InjectMocks
    private ErrorAspect errorAspect;

    @Test
    @DisplayName("Вызывается соответсвующий метод при MessageParseException")
    public void invokeHandlerMessageParseExceptionWhenException() throws Throwable {
        // настройка ожидаемого значения
        MessageParseException thrown = new MessageParseException(ErrorMessage.PARSE_EXCEPTION_MESSAGE.getMessage());
        // настраиваем поведение
        when(joinPointMock.getArgs()).thenReturn(new Object[]{1L});
        when(joinPointMock.proceed()).thenThrow(thrown);
        // вызываем тестируемый метод
        errorAspect.handleError(joinPointMock);
        // тест
        verify(errorHandlerService, times(1))
                .handlerMessageParseException(thrown, 1L);
    }

    @Test
    @DisplayName("Вызывается соответсвующий метод при CommandNotFoundException")
    public void invokeHandlerCommandNotFoundWhenException() throws Throwable {
        // настройка ожидаемого значения
        CommandNotFoundException thrown = new CommandNotFoundException("");
        // настройка поведения
        when(joinPointMock.getArgs()).thenReturn(new Object[]{1L});
        when(joinPointMock.proceed()).thenThrow(thrown);
        // вызываем тестируемый метод
        errorAspect.handleError(joinPointMock);
        // тест
        verify(errorHandlerService, times(1))
                .handlerCommandNotFoundException(thrown, 1L);
    }
}
