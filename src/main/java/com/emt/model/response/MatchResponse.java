package com.emt.model.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record MatchResponse(String winnerName, String loserName, Instant createdAt) {
}
