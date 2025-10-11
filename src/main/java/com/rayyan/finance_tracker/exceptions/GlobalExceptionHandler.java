package com.rayyan.finance_tracker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rayyan.finance_tracker.exceptions.SavingsException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InsufficientFundsException.class)
  public ResponseEntity<Map<String, Object>> insufficientFundsException(InsufficientFundsException e) {
    Map<String, Object> response = new HashMap<>();
    response.put("message", e.getMessage());
    response.put("StatusCode", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Withdrawal Error");
    response.put("timestamp", LocalDateTime.now().toString());

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(SavingsException.class)
  public ResponseEntity<Map<String, Object>> SavingsException(SavingsException e) {
    Map<String, Object> response = new HashMap<>();
    response.put("message", e.getMessage());
    response.put("StatusCode", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Saving Goal Exception");
    response.put("timestamp", LocalDateTime.now().toString());

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidAmountException.class)
  public ResponseEntity<Map<String, Object>> InvalidAmountException(InvalidAmountException e) {
    Map<String, Object> response = new HashMap<>();
    response.put("message", e.getMessage());
    response.put("StatusCode", HttpStatus.FORBIDDEN.value());
    response.put("error", "Withdrawal Error");
    response.put("timestamp", LocalDateTime.now().toString());

    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(DuplicateCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handelDuplicateEntry(DuplicateCredentialsException e) {
    Map<String, Object> response = new HashMap<>();
    response.put("message", e.getMessage());
    response.put("StatusCode", HttpStatus.CONFLICT.value());
    response.put("error", "Duplicate Credentials");
    response.put("timestamp", LocalDateTime.now().toString());

    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  // Handles Transaction not found Exception
  @ExceptionHandler(TransactionNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleTransactionNotFoundException(TransactionNotFoundException e) {

    Map<String, Object> response = new HashMap<>();
    response.put("message", e.getMessage());
    response.put("StatusCode", HttpStatus.NOT_FOUND.value());
    response.put("error", "Transaction not found");
    response.put("timestamp", LocalDateTime.now().toString());

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, Object>> handelValidationException(ValidationException e) {

    Map<String, Object> response = new HashMap<>();
    response.put("message", e.getMessage());
    response.put("StatusCode", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Validation Failure");
    response.put("timestamp", LocalDateTime.now().toString());

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
  /*
   * Returns into a json format
   * {
   * "message": "Transaction Type cannot be empty",
   * "error": "Validation Failure",
   * "StatusCode": 400,
   * "timestamp": "2025-09-17T01:17:42.848354200"
   * }
   */

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    return new ResponseEntity<>("An Error Occurred: " + e.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
