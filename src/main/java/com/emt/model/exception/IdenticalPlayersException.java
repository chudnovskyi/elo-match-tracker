package com.emt.model.exception;

public class IdenticalPlayersException extends RuntimeException {
  public IdenticalPlayersException() {
    super("A match cannot be created with identical players.");
  }
}
