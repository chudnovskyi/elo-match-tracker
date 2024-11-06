package com.emt.model.response;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record PlayerResponse(
    Long playerId, String nickname, BigDecimal eloRating, Instant registeredAt) {}
