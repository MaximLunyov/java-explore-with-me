DROP TABLE IF EXISTS hits;

CREATE TABLE IF NOT EXISTS Hits
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    ip        VARCHAR(50)  NOT NULL,
    uri       VARCHAR(140) NOT NULL,
    app       VARCHAR(140) NOT NULL,
    timestamp TIMESTAMP    NOT NULL
);