package pro.sky.telegrambot.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Аспект логгирования для каждого публичного метода сервисов и компонентов
@Aspect
@Component
public class LoggerAspect {
    private final Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    @Pointcut("execution(* pro.sky.telegrambot.service..*(..)) || execution(* pro.sky.telegrambot.component..*(..)) ")
    public void applicationPackagePointcut() {

    }

    @Before("applicationPackagePointcut()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        logger.info("Entering method: {} with arguments: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }
}
