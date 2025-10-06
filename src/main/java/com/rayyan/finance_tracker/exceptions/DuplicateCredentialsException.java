package com.rayyan.finance_tracker.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuplicateCredentialsException extends RuntimeException {

  private static final Logger logger = LoggerFactory.getLogger(ValidationException.class);

  public DuplicateCredentialsException(String message) {
    super(message);
    logger.warn("Duplicate Resource {}", message);
  }

}
