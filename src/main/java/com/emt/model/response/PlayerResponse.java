package com.emt.model.response;

import java.time.Instant;
import lombok.Builder;

@Builder
public record PlayerResponse(
    Long playerId, String nickname, Integer eloRating, Instant registeredAt) {}
