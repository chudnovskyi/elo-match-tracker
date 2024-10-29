CREATE TABLE match
(
    match_id   BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    winner_id  BIGINT                   NOT NULL REFERENCES player (player_id),
    loser_id   BIGINT                   NOT NULL REFERENCES player (player_id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
