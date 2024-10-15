package com.emt.model.response;

import com.emt.entity.Player;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CreateMatchResponse(
    Long matchId, Player playerOneName, Player playerTwoName, Instant matchTime, String winner) {}
