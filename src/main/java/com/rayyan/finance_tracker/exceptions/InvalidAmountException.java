package com.rayyan.finance_tracker.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidAmountException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(InvalidAmountException.class);
    public InvalidAmountException(String message) {
      super(message);
      logger.error("Error: {}",message);
    }
}
