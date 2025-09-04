package com.rayyan.finance_tracker.exceptions;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ValidationException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(ValidationException.class);
    public ValidationException(String message) {
        super(message);
        logger.warn("Validation Failed {}", message);
    }
}
