ALTER TABLE match
    ADD COLUMN winner_rating_change NUMERIC(10, 2) NOT NULL DEFAULT 0,
    ADD COLUMN loser_rating_change  NUMERIC(10, 2) NOT NULL DEFAULT 0;