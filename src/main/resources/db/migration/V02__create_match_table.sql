CREATE TABLE match
(
    match_id   BIGSERIAL PRIMARY KEY,
    winner_id  BIGINT                   NOT NULL,
    loser_id   BIGINT                   NOT NULL,
    match_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    outcome    VARCHAR(10)              NOT NULL,
    CONSTRAINT fk_winner FOREIGN KEY (winner_id) REFERENCES player (player_id),
    CONSTRAINT fk_loser FOREIGN KEY (loser_id) REFERENCES player (player_id)
);
