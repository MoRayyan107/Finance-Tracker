package com.rayyan.finance_tracker.exceptions;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/*
    This Exception class handles those transactions that are NOT FOUND
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(TransactionNotFoundException.class);

    public TransactionNotFoundException(String message) {
        super(message);
        logger.warn("Error: {}", message);
    }
}
