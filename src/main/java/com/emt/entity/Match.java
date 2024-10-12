package com.emt.entity;

import com.emt.entity.enums.MatchOutcome;
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
  @JoinColumn(name = "winner_id", nullable = false)
  private Player winner;

  @NotNull(message = "Player 2 should not be null.")
  @ManyToOne
  @JoinColumn(name = "loser_id", nullable = false)
  private Player loser;

  @Column(name = "match_date", updatable = false)
  private Instant matchDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "outcome", nullable = false)
  private MatchOutcome outcome;
}
