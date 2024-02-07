package com.template.model.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(Long userId) {
    super("User with id %s not found".formatted(userId));
  }
}
