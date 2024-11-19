package com.emt.model.response;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record MatchResponse(
    Long matchId,
    String winnerName,
    String loserName,
    Instant createdAt,
    BigDecimal winnerRatingChange) {}
