package com.emt.model.exception;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(Long playerId) {
        super("Player with id %s not found".formatted(playerId));
    }
}
