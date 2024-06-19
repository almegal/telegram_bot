package pro.sky.telegrambot.service.integration;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static pro.sky.telegrambot.ConfigurationTest.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotificationTaskServiceIntegrationTest {
    @SpyBean
    private NotificationTaskRepository repository;
    @Autowired
    private NotificationTaskService service;

    @Test
    @DisplayName("Сохранение через метод сервиса")
    public void shouldSuccessSaveTask() {
        // вызываем тестируемый метод
        service.saveTask(MOCK_TASK_DEFAULT);
        // получаем сохраненную в бд задачу
        NotificationTask actual = repository.findById(1L).orElse(new NotificationTask());
        // првоеряем что метод сохраения вызван 1 раз
        verify(repository, times(1)).save(MOCK_TASK_DEFAULT);
        // сверяем результаты сохраненного значенимя
        assertEquals(MOCK_TASK_DEFAULT, actual);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("Получание списка задач")
    public void shouldReturnCorrectList() {
        // сохраним в бд нескольких задач для теста
        MOCK_LIST_TASK.forEach(t -> repository.save(t));
        // подготовка актуального значения
        List<NotificationTask> actual = service.getByDateAndTimeNow(MOCK_LOCAL_DATE_TIME);
        // подготовка ожидаемого значения
        List<NotificationTask> expected = MOCK_LIST_TASK.stream()
                .filter(t -> MOCK_LOCAL_DATE_TIME.equals(t.getNotificationDateTime()))
                .collect(Collectors.toList());
        // проверяем что вызывается метод репозитория
        verify(repository, times(1)).getByDateAndTimeNow(MOCK_LOCAL_DATE_TIME);
        // если порядок не важен
        assertTrue(CollectionUtils.isEqualCollection(expected, actual));
    }
}
