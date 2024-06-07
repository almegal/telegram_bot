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
    private final long chat_id;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private long id;
    @NotNull
    private boolean is_active;
    @NotNull
    private LocalTime notification_time;
    @NotNull
    private LocalDate notification_date;
    @NotNull
    private LocalDateTime created_at;
    @NotNull
    private String task;

    public NotificationTask(long chat_id,
                            String task,
                            LocalTime notification_time,
                            LocalDate notification_date) {
        this.chat_id = chat_id;
        this.notification_time = notification_time;
        this.notification_date = notification_date;
        this.task = task;
        this.is_active = true;
        this.created_at = LocalDateTime.now();
    }

    public long getChat_id() {
        return chat_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public LocalTime getNotification_time() {
        return notification_time;
    }

    public void setNotification_time(LocalTime notification_time) {
        this.notification_time = notification_time;
    }

    public LocalDate getNotification_date() {
        return notification_date;
    }

    public void setNotification_date(LocalDate notification_date) {
        this.notification_date = notification_date;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
