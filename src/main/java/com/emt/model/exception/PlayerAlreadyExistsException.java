package com.emt.model.exception;

public class PlayerAlreadyExistsException extends RuntimeException {
  public PlayerAlreadyExistsException(String username) {
    super("Player with username %s already exists.".formatted(username));
  }
}
