package com.template.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @JoinColumn(name = "player1_id", nullable = false)
    private Player player1;

    @NotNull(message = "Player 2 should not be null.")
    @ManyToOne
    @JoinColumn(name = "player2_id", nullable = false)
    private Player player2;

    @Column(name = "player1_score", nullable = false)
    private int player1Score;

    @Column(name = "player2_score", nullable = false)
    private int player2Score;

    @Column(name = "match_date", updatable = false)
    private LocalDateTime matchDate = LocalDateTime.now();
}
