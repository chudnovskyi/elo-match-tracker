package com.emt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(
    name = "player",
    uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long playerId;

  @NotEmpty(message = "Username should not be empty.")
  @Size(
      min = 5,
      max = 50,
      message = "The length of the username must be from 5 to 50 characters inclusive.")
  private String username;

  @NotNull
  @Column(name = "elo_rating", nullable = false, columnDefinition = "INTEGER DEFAULT 1200")
  private Integer eloRating;

  @Column(name = "registered_at", updatable = false)
  private Instant registeredAt;
}
