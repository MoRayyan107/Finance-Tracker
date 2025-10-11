package com.rayyan.finance_tracker.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SavingsException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(SavingsException.class);

    public SavingsException(String message) {
        super(message);
        logger.error("SavingsException: {}" , message);
    }
}
