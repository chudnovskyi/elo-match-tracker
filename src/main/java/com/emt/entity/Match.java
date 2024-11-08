package com.emt.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.emt.model.internal.EloRatingChange;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "winner_id")
  private Player winner;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "loser_id")
  private Player loser;

  @NotNull private Instant createdAt;

  @Embedded private EloRatingChange ratingChange;
}
