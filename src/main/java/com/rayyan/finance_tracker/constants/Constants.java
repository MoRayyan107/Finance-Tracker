package com.rayyan.finance_tracker.constants;

public class Constants {

    /* constant values for Password and Username Check */
    public static final int MIN_USERNAME_LENGTH = 4;
    public static final int MAX_USERNAME_LENGTH = 20;

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 50;

    // Email Validation constants
    public static final int MIN_EMAIL_LENGTH = 5;
    public static final int MAX_EMAIL_LENGTH = 50;

    // prevention instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

}
