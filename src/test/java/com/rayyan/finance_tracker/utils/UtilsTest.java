package com.rayyan.finance_tracker.utils;

import org.junit.jupiter.api.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rayyan.finance_tracker.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UtilsTest {

    // string the errors
    private List<String> errors;
    private static final Map<Integer,String> test_Passes = new HashMap<>();

    @BeforeEach
    public void setUp(){
        errors = new ArrayList<>();
    }

    /* *********************** Username Checks *********************** */

    @Test
    void CheckUsername_Valid_Display_NoError(){
        ValidatingUtil.checkIsEmpty(VALID_USERNAME,USERNAME,errors);
        ValidatingUtil.checkMinLength(VALID_USERNAME,USERNAME,MIN_USERNAME_LENGTH,errors);
        ValidatingUtil.checkMaxLength(VALID_USERNAME,USERNAME,MAX_USERNAME_LENGTH,errors);

        assertEquals(0,errors.size()); // no Errors to exist if its valid

        test_Passes.put(1,"Test 1 (CheckUsername_Valid_Display_NoError): PASS");
    }

    @Test
    void checkUsername_Empty_Displays_Error(){
        ValidatingUtil.checkIsEmpty(NULL_OR_EMPTY_USERNAME,"Username", errors);

        assertEquals(1, errors.size());
        assertEquals("Username can't be null or empty", errors.get(0));

        test_Passes.put(2,"Test 2 (checkUsername_Empty_Displays_Error): PASS");
    }

    @Test
    void CheckUsername_MinLength_Displays_Error(){
        ValidatingUtil.checkMinLength(SHORT_USERNAME,"Username",MIN_USERNAME_LENGTH, errors);

        assertEquals(1, errors.size());
        assertEquals("Username can't be less than " + MIN_USERNAME_LENGTH +" characters", errors.get(0));

        test_Passes.put(3, "Test 3 (checkUsername_MinLength_Displays_Error): PASS");
    }

    @Test
    void CheckUsername_MaxLength_Displays_Error(){
        ValidatingUtil.checkMaxLength(LONG_USERNAME,"Username",MAX_USERNAME_LENGTH, errors);

        assertEquals(1, errors.size());
        assertEquals("Username can't be greater than " + MAX_USERNAME_LENGTH +" characters", errors.get(0));

        test_Passes.put(4,"Test 4 (checkUsername_MaxLength_Displays_Error): PASS");
    }

    /* *********************** Password Checks *********************** */
    @Test
    void CheckPassword_Valid_Display_NoError(){
        ValidatingUtil.checkIsEmpty(VALID_PASSWORD,PASSWORD,errors);
        ValidatingUtil.checkMinLength(VALID_PASSWORD,PASSWORD,MIN_USERNAME_LENGTH,errors);
        ValidatingUtil.checkMaxLength(VALID_PASSWORD,PASSWORD,MAX_PASSWORD_LENGTH,errors);

        assertEquals(0,errors.size()); // no Errors

        test_Passes.put(5, "Test 5 (checkPassword_Valid_Display_NoError): PASS");
    }

    @Test
    void CheckPassword_Empty_Displays_Error(){
        ValidatingUtil.checkIsEmpty(NULL_OR_EMPTY_PASSWORD,PASSWORD,errors);

        assertEquals(1, errors.size());
        assertEquals(PASSWORD+" can't be null or empty", errors.get(0));

        test_Passes.put(6, "Test 6 (checkPassword_Empty_Displays_Error): PASS");
    }

    @Test
    void CheckPassword_MinLength_Displays_Error(){
        ValidatingUtil.checkMinLength(SHORT_PASSWORD,PASSWORD,MIN_USERNAME_LENGTH, errors);

        assertEquals(1, errors.size());
        assertEquals(PASSWORD+" can't be less than " + MIN_USERNAME_LENGTH + " characters", errors.get(0));

        test_Passes.put(7, "Test 7 (CheckPassword_MinLength_Displays_Error): PASS");
    }

    @Test
    void CheckPassword_MaxLength_Displays_Error(){
        ValidatingUtil.checkMaxLength(LONG_PASSWORD,PASSWORD,MAX_PASSWORD_LENGTH, errors);

        assertEquals(1, errors.size());
        assertEquals(PASSWORD+" can't be greater than " + MAX_PASSWORD_LENGTH + " characters", errors.get(0));

        test_Passes.put(8, "Test 8 (CheckPassword_MaxLength_Displays_Error): PASS");
    }

    @AfterAll
    static void afterAll(){
        int maxLength = 0;
        // We create a temporary list to hold just the test names
        List<String> testNames = new ArrayList<>();
        final int totalTests = 8; // each Username and Password had 4 tests
        int passedTests = 0;

        for (String result : test_Passes.values()) {
            // Remove the ": Pass" part to get the actual test name
            String testName = result.replace(": Pass", "");
            testNames.add(testName);

            // Find the longest name
            if (testName.length() > maxLength) {
                maxLength = testName.length();
            }
            passedTests++;
        }

        // --- Step 2: Create a dynamic format string ---
        // This creates a format like "%-50s %s%n", where 50 is the max length
        // The "%-" means left-justify and pad with spaces.
        String format = "%-" + (maxLength) + "s    -> %s%n";

        // --- Step 3: Print the results using the new format ---
        System.out.println("\n--- AuthController Test Results ---");
        for (String name : testNames) {
            System.out.printf(format, name, "Pass");
        }
        System.out.println("---------------------------------");

        if (passedTests == totalTests)
            System.out.println("SUMMARY: All " + passedTests + "/" + totalTests + " tests passed!");
        else
            System.out.println("SUMMARY: " + passedTests + "/" + totalTests + " tests passed.");
        System.out.println("---------------------------------");
    }

}
