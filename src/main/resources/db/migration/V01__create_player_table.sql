CREATE TABLE player
(
    player_id     BIGSERIAL PRIMARY KEY,
    nickname      TEXT      NOT NULL UNIQUE,
    elo_rating    INTEGER            DEFAULT 1200 NOT NULL,
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);