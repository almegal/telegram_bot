package pro.sky.telegrambot.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.exception.CommandNotFoundException;
import pro.sky.telegrambot.exception.DateTimeBeforeException;
import pro.sky.telegrambot.exception.MessageParseException;
import pro.sky.telegrambot.service.ErrorHandlerService;

/*
 * Аспект для обработки ошибок
 * */
@Aspect
@Component
public class ErrorAspect {
    //    для логирования создаем логгер
    private final Logger logger = LoggerFactory.getLogger(ErrorAspect.class);
    //    внедряем сервис обработки ошибок
    private final ErrorHandlerService errorHandlerService;

    public ErrorAspect(ErrorHandlerService errorHandlerService) {
        this.errorHandlerService = errorHandlerService;
    }

    @Pointcut("execution(* pro.sky.telegrambot.service.HandlerMessageService.handleUpdateMessage(..)) " +
            "|| execution(* pro.sky.telegrambot.service.HandlerMessageService.handleUpdateCommand(..)) ")
    public void pointCutError() {
    }

    //    Для методов HandlerMessageService.handleUpdateMessage
    //    HandlerMessageService.handleUpdateCommand
    //    Вызываем аспект
    @Around("pointCutError()")
    public Object handleError(ProceedingJoinPoint joinPoint) {
        //    Получаем первый аргумент методов
        long chatId = (long) joinPoint.getArgs()[0];
        //    Пробуем выполнить сам метод
        try {
            return joinPoint.proceed();
            //    Перехватываем ошибки и вызываем в зависимости от типа ошибок
            //    метод для обработки
        } catch (DateTimeBeforeException e) {
            logger.error(e.getMessage(), e);
            errorHandlerService.handleDateTimeBeforeException(e, chatId);
            return null;
        } catch (MessageParseException e) {
            logger.error(e.getMessage(), e);
            errorHandlerService.handlerMessageParseException(e, chatId);
            return null;
        } catch (CommandNotFoundException e) {
            logger.error(e.getMessage(), e);
            errorHandlerService.handlerCommandNotFoundException(e, chatId);
            return null;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            errorHandlerService.handlerThrowableException(e, chatId);
            return null;
        }
    }
}
