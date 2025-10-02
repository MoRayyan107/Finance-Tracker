package com.rayyan.finance_tracker;

import org.hibernate.AssertionFailure;

public class TestConstants {

    // JWT Token Dummy
    public static final String DUMMY_JWT_TOKEN = "DUMMY_JWT_TOKEN_GENERATED_123";;

    // field names (Username and Password)
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";

    // Valid Username and Password
    public static final String VALID_USERNAME = "testUser123";
    public static final String VALID_PASSWORD = "testPassword123";

    // Invalid Usernames
    public static final String LONG_USERNAME = "Pseudopseudohypoparathyroidism"; // more than 20 characters
    public static final String SHORT_USERNAME = "AC"; // less than 4 characters
    public static final String NULL_OR_EMPTY_USERNAME = "";

    // Invalid Passwords
    public static final String LONG_PASSWORD = "Pneumonoultramicroscopicsilicovolcanoconiosisassupercalifragilisticexpialidocious"; // more than 50 characters
    public static final String SHORT_PASSWORD = "ABC"; // less than 8 characters
    public static final String NULL_OR_EMPTY_PASSWORD = "";

    // constant values for Password and Username Check
    public static final int MIN_USERNAME_LENGTH = 4;
    public static final int MAX_USERNAME_LENGTH = 20;

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 50;

    // prevention instantiation
    private TestConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
