CREATE TABLE player
(
    player_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(100) UNIQUE NOT NULL,
    elo_rating INT DEFAULT 1000,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE match
(
    match_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    player1_id BIGINT NOT NULL,
    player2_id BIGINT NOT NULL,
    player1_score INT NOT NULL,
    player2_score INT NOT NULL,
    match_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player1_id) REFERENCES player(player_id),
    FOREIGN KEY (player2_id) REFERENCES player(player_id)
);

