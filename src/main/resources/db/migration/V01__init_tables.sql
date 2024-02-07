CREATE TABLE user_
(
    user_id     BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    first_name  TEXT   NOT NULL,
    last_name   TEXT   NOT NULL
);
