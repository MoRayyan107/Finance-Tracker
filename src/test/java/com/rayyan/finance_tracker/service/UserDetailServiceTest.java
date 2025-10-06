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
import java.util.TreeMap;

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

  private static final Map<Integer, String> test_Passes = new HashMap<>();

  @BeforeAll
  static void beforeAll() {
    System.out.println("\n--- Starting UserDetailService Tests ---");
  }

  @BeforeEach
  void setup() {
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
    assertEquals(EXISTING_USERNAME, userDetails.getUsername());
    verify(userRepository, times(1)).findByUsername(EXISTING_USERNAME);

    // record the test as passed
    test_Passes.put(1, "LoadByUsername: Existing Username Success");
  }

  @Test
  void test_LoadByUsername_Non_Existing_Username_ThrowsEception() {
    // Arrange
    when(userRepository.findByUsername(NON_EXISTING_USERNAME))
        .thenThrow(new UsernameNotFoundException("Username Not Found: " + NON_EXISTING_USERNAME));

    // Act & Assert
    assertThrows(UsernameNotFoundException.class, () -> {
      userDetailService.loadUserByUsername(NON_EXISTING_USERNAME);
    }, "Expected UsernameNotFoundException to be thrown");

    verify(userRepository, times(1)).findByUsername(NON_EXISTING_USERNAME);

    // record the test as passed
    test_Passes.put(2, "LoadByUsername: Non-Existing Username Throws Exception");
  }

  /* ************** getUserByUsername Test cases ************* */
  @Test
  void test_GetByUsername_Existing_Username_Success() {
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
    test_Passes.put(3, "GetUserByUsername: Existing Username Success");
  }

  @Test
  void test_GetUserByUsername_Non_Existing_Username_ThrowsError() {
    // Arrange
    when(userRepository.findByUsername(NON_EXISTING_USERNAME))
        .thenThrow(new UsernameNotFoundException("Username Not Found: " + NON_EXISTING_USERNAME));

    // Act & Assert
    assertThrows(UsernameNotFoundException.class, () -> {
      userDetailService.getUserByUsername(NON_EXISTING_USERNAME);
    }, "Expected UsernameNotFoundException to be thrown");

    verify(userRepository, times(1)).findByUsername(NON_EXISTING_USERNAME);

    // record the test as passed
    test_Passes.put(4, "GetUserByUsername: Non-Existing Username Throws Exception");
  }

  /* Printing Test Case Passes */
  @AfterAll
  static void afterAll() {
    int maxLength = 0;
    int totalTests = 4;
    int passedTests = test_Passes.size();

    // Separate tests into LoadByUsername and GetUserByUsername groups
    Map<Integer, String> loadByUsernameTests = new TreeMap<>();
    Map<Integer, String> getUserByUsernameTests = new TreeMap<>();

    for (Map.Entry<Integer, String> entry : test_Passes.entrySet()) {
      String testName = entry.getValue();
      if (testName.startsWith("LoadByUsername:")) {
        loadByUsernameTests.put(entry.getKey(), testName);
      } else if (testName.startsWith("GetUserByUsername:")) {
        getUserByUsernameTests.put(entry.getKey(), testName);
      }

      // Find the longest name
      if (testName.length() > maxLength) {
        maxLength = testName.length();
      }
    }

    // Create format string
    String format = "Test %-2d: %-" + maxLength + "s -> %s%n";

    // Print results
    System.out.println("\n--- UserDetailService Test Results ---");

    // Print LoadByUsername tests
    System.out.println("\n=== LOAD BY USERNAME TESTS ===");
    for (Map.Entry<Integer, String> entry : loadByUsernameTests.entrySet()) {
      System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
    }

    // Print GetUserByUsername tests
    System.out.println("\n=== GET USER BY USERNAME TESTS ===");
    for (Map.Entry<Integer, String> entry : getUserByUsernameTests.entrySet()) {
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