package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    // получить весь список нотификаций по дате и времени
    @Query(value = "select * from notification_task nt " +
            "where nt.notification_date = :localDate " +
            "and nt.notification_time = :localTime", nativeQuery = true)
    List<NotificationTask> getByDateAndTimeNow(@Param("localDate") LocalDate localDate,
                                               @Param("localTime") LocalTime localTime);
}
