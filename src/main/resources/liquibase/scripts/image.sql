-- liquibase formatted sql

-- changeSet ivan:2

CREATE TABLE images
(
    image_id   SERIAL NOT NULL PRIMARY KEY,
    file_path  TEXT   NOT NULL,
    file_size  BIGINT NOT NULL,
    media_type TEXT   NOT NULL,
    image_data BIGINT NOT NULL
);