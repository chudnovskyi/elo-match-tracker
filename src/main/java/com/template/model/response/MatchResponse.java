package com.template.model.response;

import java.time.LocalDateTime;

public record MatchResponse(
        Long matchId,
        String player1Name,
        String player2Name,
        int player1Score,
        int player2Score,
        LocalDateTime matchDate,
        String outcome
) { }
