-- liquibase formatted sql

-- changeset alexey:1
CREATE TABLE notification_task (
    id SERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    notification_time TIME NOT NULL,
    notification_date DATE NOT NULL,
    created_at DATE NOT NULL,
    task VARCHAR(255) NOT NULL
)