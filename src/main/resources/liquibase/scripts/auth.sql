-- liquibase formatted sql

-- changeSet ivan:1

CREATE TABLE IF NOT EXISTS users
(
    username TEXT    NOT NULL PRIMARY KEY,
    password TEXT    NOT NULL,
    enabled  BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS authorities
(
    username  TEXT NOT NULL PRIMARY KEY,
    authority TEXT NOT NULL,
    FOREIGN KEY (username) references users (username)
);

CREATE TABLE user_details
(
    user_id         SERIAL NOT NULL PRIMARY KEY,
    user_first_name TEXT,
    user_last_name  TEXT,
    user_phone      TEXT
);