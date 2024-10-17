CREATE TABLE player
(
    player_id     BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nickname      TEXT      NOT NULL UNIQUE,
    elo_rating    INTEGER            DEFAULT 1200 NOT NULL,
    registered_at TIMESTAMP NOT NULL DEFAULT now()
);
