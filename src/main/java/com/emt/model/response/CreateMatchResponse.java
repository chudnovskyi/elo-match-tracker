package com.emt.model.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record CreateMatchResponse(
    Long matchId, String playerOneName, String playerTwoName, Instant matchDate, String outcome) {}
