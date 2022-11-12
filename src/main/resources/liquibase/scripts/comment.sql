-- liquibase formatted sql

-- changeset pavel:1
CREATE TABLE comments (
    id          INT         NOT NULL    UNIQUE   PRIMARY KEY,
    idAuthor    INT         NOT NULL                        ,
    createdAt   TIMESTAMP   NOT NULL                        ,
    text        TEXT        NOT NULL
);