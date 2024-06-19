package pro.sky.telegrambot;

import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigurationTest {
    //
    public static final LocalDateTime MOCK_LOCAL_DATE_TIME = LocalDateTime.now();
    //
    public static final NotificationTask MOCK_TASK_DEFAULT = new NotificationTask(1L, "some task", MOCK_LOCAL_DATE_TIME);
    //
    public static final List<NotificationTask> MOCK_LIST_TASK = Arrays.asList(
            new NotificationTask(1L, "some task #1", MOCK_LOCAL_DATE_TIME),
            new NotificationTask(2L, "some task #2", MOCK_LOCAL_DATE_TIME.plusDays(1)),
            new NotificationTask(3L, "some task #3", MOCK_LOCAL_DATE_TIME)
    );
    //
    public static final List<NotificationTask> MOCK_EMPTY_LIST = Collections.emptyList();
}
