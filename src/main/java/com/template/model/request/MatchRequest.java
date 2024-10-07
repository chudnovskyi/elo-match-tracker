package com.template.model.request;

import jakarta.validation.constraints.NotNull;

public record MatchRequest(
        @NotNull(message = "Player 1 ID should not be null.")
        Long player1Id,

        @NotNull(message = "Player 2 ID should not be null.")
        Long player2Id,

        @NotNull(message = "Player 1 score should not be null.")
        int player1Score,

        @NotNull(message = "Player 2 score should not be null.")
        int player2Score
) { }