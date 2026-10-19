package com.dvlprmatheus.linko.api.exception;

public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException() {
    super(ExceptionMessage.LINK_NOT_FOUND);
  }

  public EntityNotFoundException(String message) {
    super(message);
  }

  public EntityNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
