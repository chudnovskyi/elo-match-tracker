CREATE TABLE player
(
    player_id     BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nickname      TEXT      NOT NULL UNIQUE,
    rating        NUMERIC(10, 1)     DEFAULT 1200.0 NOT NULL,
    registered_at TIMESTAMP NOT NULL DEFAULT now()
);
