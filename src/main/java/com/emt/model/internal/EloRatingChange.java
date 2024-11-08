package com.emt.model.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public record EloRatingChange(
    @Column(name = "winner_rating_change") BigDecimal winnerRatingChange,
    @Column(name = "loser_rating_change") BigDecimal loserRatingChange) {}
