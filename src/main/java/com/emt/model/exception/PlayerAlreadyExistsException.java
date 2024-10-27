package com.emt.model.exception;

public class PlayerAlreadyExistsException extends RuntimeException {
    public PlayerAlreadyExistsException(String nickname) {
        super("Player with nickname %s already exists.".formatted(nickname));
    }
}
