CREATE TABLE ads
(
    id              BIGINT   NOT NULL   PRIMARY KEY,
    author          BIGINT   NOT NULL              ,
    image           TEXT             ,
    price           BIGINT           ,
    title           TEXT             ,
    description     TEXT
);