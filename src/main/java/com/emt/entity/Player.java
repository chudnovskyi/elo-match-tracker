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
@Table(name = "player")
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long playerId;

  @NotNull private String nickname;

  @NotNull(message = "Elo rating cannot be null")
  @Column(name = "elo_rating")
  private Integer eloRating;

  @Column(name = "registered_at", updatable = false)
  private Instant registeredAt;
}
