package com.emt.model.request;

import jakarta.validation.constraints.NotNull;

public record CreateMatchRequest(
    @NotNull(message = "Player 1 ID should not be null.") Long playerOneId,
    @NotNull(message = "Player 2 ID should not be null.") Long playerTwoId,
    @NotNull(message = "Winner ID should not be null.") Long winnerId) {}
