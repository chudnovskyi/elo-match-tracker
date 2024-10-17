package com.emt.model.response;

import java.time.Instant;
import lombok.Builder;

@Builder
public record MatchResponse(String winnerName, String looserName, Instant createdAt) {}
