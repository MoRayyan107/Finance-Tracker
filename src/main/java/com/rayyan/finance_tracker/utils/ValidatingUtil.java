package com.rayyan.finance_tracker.utils;

import com.rayyan.finance_tracker.exceptions.ValidationException;

import java.util.List;

public class ValidatingUtil {

    /**
     * Method checks if the Username or Password is Empty ("") or Null
     *
     * @param value the RegisterRequest/AuthenticationRequest Request (Username OR Password)
     * @param fieldName Which field is it checking, Password or Username
     * @param errors an Empty OR non-empty list of exceptions stored for the given request
     */
    public static void checkIsEmpty(String value, String fieldName, List<String> errors){
        if (value == null || value.trim().isEmpty())
            errors.add(fieldName + " can't be null or empty");
    }

    /**
     * This method checks the minimum length of the username and password
     *
     * @param Value the RegisterRequest/AuthenticationRequest Request (Username OR Password)
     * @param fieldName Which field is it checking, Password or Username
     * @param minLength minimum length for a password and username
     * @param errors an Empty OR non-empty list of exceptions stored for the given request
     */
    public static void checkMinLength(String Value, String fieldName, int minLength,  List<String> errors){
        if (Value != null && Value.length() < minLength)
            errors.add(fieldName + " can't be less than "+minLength+" characters");
    }

    /**
     * This method checks the maximum length of the username and password
     *
     * @param Value the RegisterRequest/AuthenticationRequest Request (Username OR Password)
     * @param fieldName Which field is it checking, Password or Username
     * @param maxLength maximum length for a password and username
     * @param errors an Empty OR non-empty list of exceptions stored for the given request
     */
    public static void checkMaxLength(String Value, String fieldName, int maxLength,  List<String> errors){
        if (Value != null && Value.length() > maxLength)
            errors.add(fieldName + " can't be greater than "+maxLength+" characters");
    }

    /**
     * Throws Exceptions if Exists in a list
     *
     * @param errors takes in a list of errors, and then throw in one single Exception
     */
    public static void throwIfExists(List<String> errors){
        if (!errors.isEmpty()){
            throw new ValidationException(String.join(", ", errors));
        }
    }
}
