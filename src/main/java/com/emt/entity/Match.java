package com.emt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "match")
public class Match {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long matchId;

  @NotNull(message = "Player 1 should not be null.")
  @ManyToOne
  @JoinColumn(name = "player_one")
  private Player playerOne;

  @NotNull(message = "Player 2 should not be null.")
  @ManyToOne
  @JoinColumn(name = "player_two")
  private Player playerTwo;

  @NotNull(message = "Winner should not be null.")
  private String winner;

  @Column(name = "match_time", updatable = false)
  private Instant matchTime;
}
