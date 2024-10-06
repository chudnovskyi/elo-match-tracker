package com.template.model.exception;

public class PlayerAlreadyExistsException extends RuntimeException {
    public PlayerAlreadyExistsException(String username) {
        super("Player with username '" + username + "' already exists.");
    }
}
