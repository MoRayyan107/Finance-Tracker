package com.rayyan.finance_tracker;

public class TestConstants {

    // Dummy Secret Key
    public static final String TEST_SECRET_KEY = "NzIzY2JiM2YtM2YyZi00NjJjLTg2YzYtYjJkYmE0NTU5ZjAx"; // Base64 encoded test key

    // JWT Token Dummy
    public static final String DUMMY_JWT_TOKEN = "DUMMY_JWT_TOKEN_GENERATED_123";;

    // field names (Username and Password)
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";

    // Valid Username and Password
    public static final String VALID_USERNAME = "testUser123";
    public static final String VALID_PASSWORD = "testPassword123";
    public static final String VALID_EMAIL = "test_User@example.com";

    // Invalid Emails
    public static final String LONG_EMAIL = "verylongemaillocalpartthatiswaytoobigandinvalid@example.com"; // more than
                                                                                                           // 50
                                                                                                           // characters
    public static final String SHORT_EMAIL = "a@b"; // less than 5 characters
    public static final String NULL_OR_EMPTY_EMAIL = "";

    // Invalid Usernames
    public static final String LONG_USERNAME = "Pseudopseudohypoparathyroidism"; // more than 20 characters
    public static final String SHORT_USERNAME = "AC"; // less than 4 characters
    public static final String NULL_OR_EMPTY_USERNAME = "";

    // Invalid Passwords
    public static final String LONG_PASSWORD = "Pneumonoultramicroscopicsilicovolcanoconiosisassupercalifragilisticexpialidocious"; // more
                                                                                                                                    // than
                                                                                                                                    // 50
                                                                                                                                    // characters
    public static final String SHORT_PASSWORD = "ABC"; // less than 8 characters
    public static final String NULL_OR_EMPTY_PASSWORD = "";

    // ivalid Emails

    // constant values for Password and Username and Email Check
    public static final int MIN_USERNAME_LENGTH = 4;
    public static final int MAX_USERNAME_LENGTH = 20;

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 50;

    public static final int MIN_EMAIL_LENGTH = 5;
    public static final int MAX_EMAIL_LENGTH = 50;

    // For User Detail Service Test
    public static final String EXISTING_USERNAME = "testUser@123";
    public static final String NON_EXISTING_USERNAME = "nonExistingUser@123";

    // Encoded Password Dummy
    public static final String TEST_ENCODED_PASSWORD = "$2a$10$Dow1b0f6dG7qJ8e3t8h8EuFvYzFh8EuFvYzFh8EuFvYzFh8EuFvYzFh8Eu"; // bcrypt
                                                                                                                            // encoded
                                                                                                                            // version
                                                                                                                            // of
                                                                                                                            // "testPassword123"

    // Dummy JWT Secret Key
    public static final String DUMMY_JWT_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    // prevention instantiation
    private TestConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
