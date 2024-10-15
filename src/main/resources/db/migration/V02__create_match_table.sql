CREATE TABLE match
(
    match_id   BIGSERIAL PRIMARY KEY,
    player_one BIGINT                   NOT NULL,
    player_two BIGINT                   NOT NULL,
    winner     TEXT                     NOT NULL,
    match_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_player_one FOREIGN KEY (player_one) REFERENCES player (player_id),
    CONSTRAINT fk_player_two FOREIGN KEY (player_two) REFERENCES player (player_id)
);
