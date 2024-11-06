CREATE TABLE player
(
    player_id     BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nickname      TEXT      NOT NULL UNIQUE,
    elo_rating    NUMERIC(10, 2)     DEFAULT 1200.00 NOT NULL,
    registered_at TIMESTAMP NOT NULL DEFAULT now()
);
