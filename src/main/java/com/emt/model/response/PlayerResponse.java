package com.emt.model.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PlayerResponse(
    Long playerId, String nickname, Integer eloRating, Instant registeredAt) {}
