ALTER TABLE match ADD COLUMN rating_change NUMERIC(10, 1) NOT NULL;
ALTER TABLE match ADD COLUMN winner_rating NUMERIC(10, 1) NOT NULL;
ALTER TABLE match ADD COLUMN loser_rating NUMERIC(10, 1) NOT NULL;
