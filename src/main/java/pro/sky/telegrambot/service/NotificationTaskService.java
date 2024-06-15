package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationTaskService {
    private final NotificationTaskRepository repository;

    public NotificationTaskService(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    // сохранить задачу в бд
    public void saveTask(NotificationTask task) {
        repository.save(task);
    }

    // найти все задачи текущего чата
    public List<NotificationTask> getActiveTask(Long chatId) {
        return repository.findByIdWhereIsActive(chatId);
    }

    // получить список задач с подходящим временем
    public List<NotificationTask> getByDateAndTimeNow(LocalDateTime localDateTime) {
        return repository.getByDateAndTimeNow(localDateTime);
    }

    // деактивировать задачу
    public void deactiveTask(Long taskId) {
        //  получить задачу
        NotificationTask task = repository.findById(taskId).orElse(new NotificationTask());
        // деактивировать
        task.setActive(false);
        // обновить
        repository.save(task);
    }
}
