package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;


// Сервис для взаимодейтсвия с БД
@Service
public class NotificationTaskService {
    private final NotificationTaskRepository repository;

    // инъекция бинов
    public NotificationTaskService(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    // сохранить задачу в бд
    public void saveTask(NotificationTask task) {
        repository.save(task);
    }

    // получить список задач с подходящим временем
    public List<NotificationTask> getByDateAndTimeNow(LocalDateTime localDateTime) {
        return repository.getByDateAndTimeNow(localDateTime);
    }
}
