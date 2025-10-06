package com.rayyan.finance_tracker.utils;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.rayyan.finance_tracker.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UtilsTest {

    private List<String> errors;
    private static final Map<Integer, String> test_Passes = new HashMap<>();

    @BeforeAll
    static void beforeAll() {
        System.out.println("\n--- Starting Utils Tests ---");
    }

    @BeforeEach
    public void setUp() {
        errors = new ArrayList<>();
    }

    /* *********************** Username Checks *********************** */

    @Test
    void CheckUsername_Valid_Display_NoError() {
        ValidatingUtil.checkIsEmpty(VALID_USERNAME, USERNAME, errors);
        ValidatingUtil.checkMinLength(VALID_USERNAME, USERNAME, MIN_USERNAME_LENGTH, errors);
        ValidatingUtil.checkMaxLength(VALID_USERNAME, USERNAME, MAX_USERNAME_LENGTH, errors);

        assertEquals(0, errors.size());

        test_Passes.put(1, "Username: Valid Display No Error");
    }

    @Test
    void checkUsername_Empty_Displays_Error() {
        ValidatingUtil.checkIsEmpty(NULL_OR_EMPTY_USERNAME, "Username", errors);

        assertEquals(1, errors.size());
        assertEquals("Username can't be null or empty", errors.get(0));

        test_Passes.put(2, "Username: Empty Displays Error");
    }

    @Test
    void CheckUsername_MinLength_Displays_Error() {
        ValidatingUtil.checkMinLength(SHORT_USERNAME, "Username", MIN_USERNAME_LENGTH, errors);

        assertEquals(1, errors.size());
        assertEquals("Username can't be less than " + MIN_USERNAME_LENGTH + " characters", errors.get(0));

        test_Passes.put(3, "Username: Min Length Displays Error");
    }

    @Test
    void CheckUsername_MaxLength_Displays_Error() {
        ValidatingUtil.checkMaxLength(LONG_USERNAME, "Username", MAX_USERNAME_LENGTH, errors);

        assertEquals(1, errors.size());
        assertEquals("Username can't be greater than " + MAX_USERNAME_LENGTH + " characters", errors.get(0));

        test_Passes.put(4, "Username: Max Length Displays Error");
    }

    /* *********************** Password Checks *********************** */
    @Test
    void CheckPassword_Valid_Display_NoError() {
        ValidatingUtil.checkIsEmpty(VALID_PASSWORD, PASSWORD, errors);
        ValidatingUtil.checkMinLength(VALID_PASSWORD, PASSWORD, MIN_USERNAME_LENGTH, errors);
        ValidatingUtil.checkMaxLength(VALID_PASSWORD, PASSWORD, MAX_PASSWORD_LENGTH, errors);

        assertEquals(0, errors.size());

        test_Passes.put(5, "Password: Valid Display No Error");
    }

    @Test
    void CheckPassword_Empty_Displays_Error() {
        ValidatingUtil.checkIsEmpty(NULL_OR_EMPTY_PASSWORD, PASSWORD, errors);

        assertEquals(1, errors.size());
        assertEquals(PASSWORD + " can't be null or empty", errors.get(0));

        test_Passes.put(6, "Password: Empty Displays Error");
    }

    @Test
    void CheckPassword_MinLength_Displays_Error() {
        ValidatingUtil.checkMinLength(SHORT_PASSWORD, PASSWORD, MIN_PASSWORD_LENGTH, errors);

        assertEquals(1, errors.size());
        assertEquals(PASSWORD + " can't be less than " + MIN_PASSWORD_LENGTH + " characters", errors.get(0));

        test_Passes.put(7, "Password: Min Length Displays Error");
    }

    @Test
    void CheckPassword_MaxLength_Displays_Error() {
        ValidatingUtil.checkMaxLength(LONG_PASSWORD, PASSWORD, MAX_PASSWORD_LENGTH, errors);

        assertEquals(1, errors.size());
        assertEquals(PASSWORD + " can't be greater than " + MAX_PASSWORD_LENGTH + " characters", errors.get(0));

        test_Passes.put(8, "Password: Max Length Displays Error");
    }

    @AfterAll
    static void afterAll() {
        int maxLength = 0;
        final int totalTests = 8;
        int passedTests = test_Passes.size();

        Map<Integer, String> usernameTests = new TreeMap<>();
        Map<Integer, String> passwordTests = new TreeMap<>();

        for (Map.Entry<Integer, String> entry : test_Passes.entrySet()) {
            String testName = entry.getValue();
            if (testName.startsWith("Username:")) {
                usernameTests.put(entry.getKey(), testName);
            } else if (testName.startsWith("Password:")) {
                passwordTests.put(entry.getKey(), testName);
            }

            if (testName.length() > maxLength) {
                maxLength = testName.length();
            }
        }

        String format = "Test %-2d: %-" + maxLength + "s -> %s%n";

        System.out.println("\n--- Utils Test Results ---");

        System.out.println("\n=== USERNAME TESTS ===");
        for (Map.Entry<Integer, String> entry : usernameTests.entrySet()) {
            System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
        }

        System.out.println("\n=== PASSWORD TESTS ===");
        for (Map.Entry<Integer, String> entry : passwordTests.entrySet()) {
            System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
        }

        System.out.println("\n---------------------------------");

        if (passedTests == totalTests)
            System.out.println("SUMMARY: All " + passedTests + "/" + totalTests + " tests passed!");
        else
            System.out.println("SUMMARY: " + passedTests + "/" + totalTests + " tests passed.");
        System.out.println("---------------------------------");
    }
}