package com.emt.model.response;

import com.emt.model.internal.EloRatingChange;
import java.time.Instant;
import lombok.Builder;

@Builder
public record MatchResponse(
    String winnerName, String loserName, Instant createdAt, EloRatingChange ratingChange) {}