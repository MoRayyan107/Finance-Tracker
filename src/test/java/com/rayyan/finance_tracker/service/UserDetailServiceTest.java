package com.rayyan.finance_tracker.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.rayyan.finance_tracker.repository.UserRepository;
import com.rayyan.finance_tracker.entity.User;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static com.rayyan.finance_tracker.TestConstants.EXISTING_USERNAME;
import static com.rayyan.finance_tracker.TestConstants.NON_EXISTING_USERNAME;
import static com.rayyan.finance_tracker.entity.User.Role;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserDetailServiceTest {

  @Mock 
  private UserRepository userRepository;

  @InjectMocks
  private UserDetailService userDetailService;

  private User testUser; // user object to test with 

  private static final Map<Integer,String> test_Passes = new HashMap<>();

  @BeforeAll
  static void beforeAll() {
    System.out.println("\n--- Starting UserDetailService Tests ---");
  }

  @BeforeEach
  void setup(){
    // create a valid test user
    testUser = User.builder()
          .id(1L)
          .username(EXISTING_USERNAME)
          .password("testPassword@123")
          .role(Role.USER)
          .build();
  }

  /* ************** LoadByUser Test cases ************* */

  @Test
  void test_LoadByUsername_Existing_Username_Success() {
    // Arrange
    when(userRepository.findByUsername(EXISTING_USERNAME))
       .thenReturn(Optional.of(testUser)); 

    // Act
    UserDetails userDetails = userDetailService.loadUserByUsername(EXISTING_USERNAME);

    // verify
    assertNotNull(userDetails);
    assertEquals(EXISTING_USERNAME,userDetails.getUsername());
    verify(userRepository, times(1)).findByUsername(EXISTING_USERNAME);

    // record the test as passed
    test_Passes.put(1,"Test 1 (test_LoadByUsername_Existing_Username_Success): PASS");
  }

  @Test
  void test_LoadByUsername_Non_Existing_Username_ThrowsEception(){
    // Arrange
    when(userRepository.findByUsername(NON_EXISTING_USERNAME))
        .thenThrow(new UsernameNotFoundException("Username Not Found: "+NON_EXISTING_USERNAME));

    // Act & Assert
    assertThrows(UsernameNotFoundException.class, () -> {
      userDetailService.loadUserByUsername(NON_EXISTING_USERNAME);
    }, "Expected UsernameNotFoundException to be thrown");

    verify(userRepository, times(1)).findByUsername(NON_EXISTING_USERNAME);

    // record the test as passed
    test_Passes.put(2,"Test 2 (test_LoadByUsername_Non_Existing_Username_ThrowsEception): PASS");
  }

  /* ************** getUserByUsername Test cases ************* */
  @Test
  void test_GetByUsername_Existing_Username_Success(){
    // Arrange 
    when(userRepository.findByUsername(EXISTING_USERNAME))
       .thenReturn(Optional.of(testUser));

    // Act
    User resultUser = userDetailService.getUserByUsername(EXISTING_USERNAME);

    // Verify
    assertNotNull(resultUser);
    assertEquals(EXISTING_USERNAME, resultUser.getUsername());
    verify(userRepository, times(1)).findByUsername(EXISTING_USERNAME);

    // record the test as passed
    test_Passes.put(3,"Test 3 (test_GetByUsername_Existing_Username_Success): PASS");
  }

  @Test
  void test_GetUserByUsername_Non_Existing_Username_ThrowsError(){
    // Arrange 
    when(userRepository.findByUsername(NON_EXISTING_USERNAME))
        .thenThrow(new UsernameNotFoundException("Username Not Found: "+NON_EXISTING_USERNAME));

    // Act & Assert
    assertThrows(UsernameNotFoundException.class, () -> {
      userDetailService.getUserByUsername(NON_EXISTING_USERNAME);
    }, "Expected UsernameNotFoundException to be thrown");

    verify(userRepository, times(1)).findByUsername(NON_EXISTING_USERNAME);

    // record the test as passed
    test_Passes.put(4,"Test 4 (test_GetUserByUsername_Non_Existing_Username_ThrowsError): PASS");
  }

  /* Printing Test Case Passes */
  @AfterAll
  static void afterAll() {
    int maxLength = 0;
    // We create a temporary list to hold just the test names
    List<String> testNames = new ArrayList<>();
    int totalTests = 4; // each loadByUsername and getUserByUsername has 2 tests
    int passedTests = 0;

    for (String result : test_Passes.values()) {
      // Remove the ": Pass" part to get the actual test name
      String testName = result.replace(": PASS", "");
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
    System.out.println("\n--- UserDetailService Test Results ---");
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
