package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private long id;
    private long chatId;
    @NotNull
    private boolean isActive;
    @NotNull
    private LocalTime notificationTime;
    @NotNull
    private LocalDate notificationDate;
    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    private String task;

    public NotificationTask() {
    }

    public NotificationTask(long chatId,
                            String task,
                            LocalTime notificationTime,
                            LocalDate notificationDate) {
        if (notificationDate != null && notificationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Дата и время не может быть раньше текущей");
        }
        if (notificationTime != null && notificationTime.isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("Дата и время не может быть раньше текущей");
        }
        this.chatId = chatId;
        this.notificationTime = notificationTime;
        this.notificationDate = notificationDate;
        this.task = task;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public long getChatId() {
        return chatId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public LocalTime getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(LocalTime notificationTime) {
        this.notificationTime = notificationTime;
    }

    public LocalDate getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDate notificationDate) {
        this.notificationDate = notificationDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
