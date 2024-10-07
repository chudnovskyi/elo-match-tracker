package com.template.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "player",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        })
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long playerId;

  @NotEmpty(message = "Username should not be empty.")
  @Size(min = 5, max = 50, message = "The length of the username must be from 5 to 50 characters inclusive.")
  private String username;

  private Integer eloRating = 1000;

  @Column(name = "registered_at", updatable = false)
  private LocalDateTime registeredAt = LocalDateTime.now();
}
