CREATE VIEW player_view AS
SELECT player_id,
       username,
       elo_rating
FROM player;

CREATE VIEW match_view AS
SELECT m.match_id,
       p1.username AS player1_username,
       p2.username AS player2_username,
       m.player1_score,
       m.player2_score,
       m.match_date
FROM match m
         JOIN player p1 ON m.player1_id = p1.player_id
         JOIN player p2 ON m.player2_id = p2.player_id;