CREATE TABLE player_
(
    player_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(100) UNIQUE NOT NULL,
    elo_rating INT DEFAULT 1000,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
