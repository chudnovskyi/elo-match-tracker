package com.emt.model.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record CreatePlayerResponse(
    Long playerId, String username, Integer eloRating, Instant registeredAt) {}