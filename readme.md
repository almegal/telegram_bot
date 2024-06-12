# Курсовая работа “Бот в Telegram”

В данной курсовой работе вам предстоит разработать бота для Telegram, 
который умел бы принимать от пользователя сообщения в формате 
`01.01.2022 20:00 Сделать домашнюю работу`
и присылать пользователю сообщение
в 20:00 1 января 2022 года с текстом “Сделать домашнюю работу”.

Цель: практическое выполнения практического задания по технологиям
>- spring/spring-boot
>- liquibase
>- postgresql
>- jpa
>- mockito test (unit & integration)
>- aspect


Базовые задачи по курсовой:
- Научиться реагировать на команду /start и выводить приветственное сообщение
- Создать использую liquibase таблицу notification_task
- Создать сущность notification task
- Создать репозиторий для сущности notification task
- Научиться парсить сообщения с создаваемым напоминанием и сохранять их в БД
-  Научиться по шедулеру раз в минуту выбирать записи из БД, для которых должны быть отправлены нотификации
- Научиться для выбранных записей рассылать уведомления

Свои улучшения к боту
- Добавить слой аспекта для логирования, парсинга сообщения и обработки ошибок
- Покрытие тестами




