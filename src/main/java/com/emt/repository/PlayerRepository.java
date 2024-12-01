package com.emt.repository;

import com.emt.entity.Player;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

  boolean existsByNickname(String nickname);

  @Modifying
  @Transactional
  @Query(
      value =
          """
          UPDATE player
          SET rating = :rating
          WHERE player_id = :playerId
          """,
      nativeQuery = true)
  void updatePlayerRating(BigDecimal rating, Long playerId);
}
