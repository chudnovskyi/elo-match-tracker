package com.emt.repository;

import com.emt.entity.Match;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Long> {

  @Query(
      """
              SELECT m
              FROM Match m
              WHERE m.createdAt > :createdAt
                AND m.isCancelled = false
                AND (m.winner.playerId IN (:winnerId, :loserId)
                     OR m.loser.playerId IN (:winnerId, :loserId))
              ORDER BY m.createdAt
              """)
  List<Match> findMatchesByPlayersAfter(
      @Param("createdAt") Instant createdAt,
      @Param("winnerId") Long winnerId,
      @Param("loserId") Long loserId);
}
