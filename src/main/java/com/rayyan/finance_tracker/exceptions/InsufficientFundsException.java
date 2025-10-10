package com.rayyan.finance_tracker.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InsufficientFundsException extends RuntimeException {
  private static final Logger logger = LoggerFactory.getLogger(InsufficientFundsException.class);

    public InsufficientFundsException(String message) {
      super(message);
      logger.error("Error: {}", message);
    }
}

