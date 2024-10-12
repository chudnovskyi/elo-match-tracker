package com.emt.repository;

import com.emt.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
  @Query(
      "SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Player p WHERE p.username = ?1")
  boolean existsByUsername(String username);
}
