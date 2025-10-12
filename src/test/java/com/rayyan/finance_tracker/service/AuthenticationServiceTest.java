package com.rayyan.finance_tracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;

import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.entity.authentication.AuthenticationRequest;
import com.rayyan.finance_tracker.entity.authentication.AuthenticationResponse;
import com.rayyan.finance_tracker.entity.authentication.RegisterRequest;
import com.rayyan.finance_tracker.exceptions.DuplicateCredentialsException;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.repository.UserRepository;
import com.rayyan.finance_tracker.service.authentication.AuthenticationService;
import com.rayyan.finance_tracker.service.jwt.JwtService;

import static com.rayyan.finance_tracker.TestConstants.*;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
  @Mock
  private JwtService jwtService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserRepository userRepository;

  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks
  private AuthenticationService authService;

  private RegisterRequest validRegisterRequest;
  private AuthenticationRequest validAuthenticationRequest;
  private User user;

  private static final Map<Integer, String> test_Passes = new HashMap<>();

  @BeforeAll
  static void beforeAll() {
    System.out.println("\n--- Starting Authentication Service Tests ---");
  }

  @BeforeEach
  void setUp() {
    validRegisterRequest = RegisterRequest.builder()
        .username(VALID_USERNAME)
        .password(VALID_PASSWORD)
        .email(VALID_EMAIL)
        .build();

    validAuthenticationRequest = AuthenticationRequest.builder()
        .username(VALID_USERNAME) // Email can be used
        .password(VALID_PASSWORD)
        .build();

    user = User.builder()
        .username(VALID_USERNAME)
        .password(TEST_ENCODED_PASSWORD)
        .email(VALID_EMAIL)
        .role(User.Role.USER)
        .build();
  }

  /* ***************************************
   * Valid Registration and Authentication
   * ***************************************
   */
  @Test
  void test_Register_ValidCredentials_Success() {
    // When
    when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(jwtService.generateToken(any(User.class))).thenReturn(DUMMY_JWT_TOKEN);

    // Given
    AuthenticationResponse response = authService.register(validRegisterRequest);

    // assert
    assertNotNull(response);
    assertEquals(DUMMY_JWT_TOKEN, response.getJwtToken());

    // verify
    verify(passwordEncoder, times(1)).encode(VALID_PASSWORD);

    // using an ArgumentCaptor to capture the user object passed to save method
    // ArgumentCaptor is a special Mockito class
    // that allows us to capture arguments passed to mocked methods
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository, times(1)).save(userCaptor.capture());

    // getting the captured user
    User capturedUser = userCaptor.getValue();

    // verify the captured user details
    assertEquals(VALID_USERNAME, capturedUser.getUsername(), "Username should match the input username");
    assertEquals(VALID_EMAIL, capturedUser.getEmail(), "Email should match the input username");
    assertEquals(TEST_ENCODED_PASSWORD, capturedUser.getPassword(), "Password should be encoded");
    assertEquals(User.Role.USER, capturedUser.getRole(), "Role should be USER");

    // verify that the JWT service was called once
    verify(jwtService, times(1)).generateToken(capturedUser);

    // test passes
    test_Passes.put(1, "Register: Valid Credentials");
  }

  @Test
  void test_Authenticate_ValidCredentials_Success() {
    // Given
    when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(user));
    when(jwtService.generateToken(any(User.class))).thenReturn(DUMMY_JWT_SECRET_KEY);

    // Stimulates a successfull login
    when(authenticationManager.authenticate(any())).thenReturn(null);

    // When
    AuthenticationResponse response = authService.authenticate(validAuthenticationRequest);

    // assert
    assertNotNull(response);
    assertEquals(DUMMY_JWT_SECRET_KEY, response.getJwtToken());

    // verify
    verify(authenticationManager, times(1)).authenticate(any());
    verify(userRepository, times(1)).findByUsername(VALID_USERNAME);
    verify(jwtService, times(1)).generateToken(user);

    // NO passwordEncoding during Authentication
    verifyNoInteractions(passwordEncoder);
    // and NO saving during authentication
    verify(userRepository, times(0)).save(any());

    // test passes
    test_Passes.put(2, "Authenticate: Valid Credentials");
  }

  /*
   * ***************************************
   * Check Duplicate during Authentication
   * ***************************************
   */
  @Test
  void test_Register_DuplicateCredentials_Username_Throws_ValidationException() {
    // Given
    when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(user));

    // assert
    assertThrows(DuplicateCredentialsException.class, () -> authService.register(validRegisterRequest),
          "Expected to Throw a DuplicateCredentalsException if any Duplicate Username found");

    // verify
    verify(userRepository, times(1)).findByUsername(VALID_USERNAME);
    verify(passwordEncoder, times(1)).encode(VALID_PASSWORD);
    verify(userRepository, never()).save(any(User.class));
    verifyNoInteractions(jwtService);

    // pass the test
    test_Passes.put(19, "Register: Duplicate Credentials (Username) Throws Exception");
  }

  @Test
  void test_Register_DuplicateCredentials_Email_Throws_ValidationException() {
    // Given
    when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(user));

    // assert
    assertThrows(DuplicateCredentialsException.class, () -> authService.register(validRegisterRequest),
        "Expected to Throw a DuplicateCredentalsException if any Duplicate email found");

    // verify
    verify(userRepository, times(1)).findByEmail(VALID_EMAIL);
    verify(passwordEncoder, times(1)).encode(VALID_PASSWORD);
    verify(userRepository, never()).save(any(User.class));
    verifyNoInteractions(jwtService);

    // pass the test
    test_Passes.put(20,"Register: Duplicate Credentials (Email) Throws Exception");
  }

  /* ***************************************
   * Username, Password and Email is Short
   * ***************************************
   */
  @Test
  void test_Register_ShortUsername_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(SHORT_USERNAME) // less than 4 characters
        .password(VALID_PASSWORD)
        .email(VALID_EMAIL)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to Throw ValidationException for Username Length " + MIN_USERNAME_LENGTH + " characters");

    // VERIFY
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // test passes
    test_Passes.put(3, "Register: Short Username Throws Validation Exception");
  }

  @Test
  void test_Register_ShortPassword_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(VALID_USERNAME)
        .password(SHORT_PASSWORD) // less than 8 characters
        .email(VALID_EMAIL)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to Throw ValidationException for Password Length " + MIN_PASSWORD_LENGTH + " characters");

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // test passes
    test_Passes.put(4, "Register: Short Password Throws Validation Exception");
  }

  @Test
  void test_Register_ShortEmail_Throws_ValidationException() {
    RegisterRequest invalidRequest = RegisterRequest.builder()
        .username(VALID_USERNAME)
        .password(VALID_PASSWORD)
        .email(SHORT_EMAIL) // Email less than 5 characters
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(invalidRequest),
        "Should Throw ValidationException for Invalid Email with length less than " + MIN_EMAIL_LENGTH);

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // pass the test
    test_Passes.put(5, "Register: Short Email Throws Validation Exception");
  }

  @Test
  void test_Authenticate_ShortUsername_Throws_ValidationException() {
    // Given
    AuthenticationRequest request = AuthenticationRequest.builder()
        .username(SHORT_USERNAME) // less than 4 characters
        .password(VALID_PASSWORD)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
        "Expected to Throw ValidationException for Username Length " + MIN_USERNAME_LENGTH + " characters");

    // VERIFY
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // test passes
    test_Passes.put(6, "Authenticate: Short Username Throws Validation Exception");
  }

  @Test
  void test_Authenticate_ShortPassword_Throws_ValidationException() {
    // Given
    AuthenticationRequest request = AuthenticationRequest.builder()
        .username(VALID_USERNAME)
        .password(SHORT_PASSWORD) // less than 8 characters
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
        "Expected to Throw ValidationException for Password Length " + MIN_PASSWORD_LENGTH + " characters");

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // test passes
    test_Passes.put(7, "Authenticate: Short Password Throws Validation Exception");
  }

  @Test
  void test_Authenticate_ShortEmail_Throws_ValidationException() {
    // Given
    AuthenticationRequest request = AuthenticationRequest.builder()
        .username(SHORT_EMAIL)
        .password(VALID_PASSWORD)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
        "Expected to throw ValidationException for Email Length " + MIN_EMAIL_LENGTH);

    // verify
    verifyNoInteractions(passwordEncoder, userRepository, jwtService);

    // pass the test
    test_Passes.put(8, "Authenticate: Short Email Throws Validation Exception");
  }

  /* ***************************************
   * Username, Password and Email is Long
   * ***************************************
   */
  @Test
  void test_Register_LongUsername_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(LONG_USERNAME) // more than 20 characters
        .password(VALID_PASSWORD)
        .email(VALID_EMAIL)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to Throw ValidationException for Username Length " + MAX_USERNAME_LENGTH + " characters");

    // VERIFY
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // test passes
    test_Passes.put(9, "Register: Long Username Throws Validation Exception");
  }

  @Test
  void test_Register_LongPassword_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(VALID_USERNAME)
        .password(LONG_PASSWORD) // more than 50 characters
        .email(VALID_EMAIL)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to Throw ValidationException for Password Length " + MAX_PASSWORD_LENGTH + " characters");

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // pass test
    test_Passes.put(10, "Register: Long Password Throws Validation Exception");
  }

  @Test
  void test_Register_LongEmail_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(VALID_USERNAME)
        .password(VALID_PASSWORD)
        .email(LONG_EMAIL)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to throw ValidationException for Email length " + MAX_EMAIL_LENGTH);

    // verify
    verifyNoInteractions(passwordEncoder, jwtService, userRepository);

    // pass the test
    test_Passes.put(11, "Register: Long Email Throws Validation Exception");
  }

  @Test
  void test_Authenticate_LongUsername_Throws_ValidationException() {
    // Given
    AuthenticationRequest request = AuthenticationRequest.builder()
        .username(LONG_USERNAME) // more than 20 characters
        .password(VALID_PASSWORD)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
        "Expected to Throw ValidationException for Username Length " + MAX_USERNAME_LENGTH + " characters");

    // VERIFY
    verifyNoInteractions(passwordEncoder, userRepository, jwtService);

    // pass test
    test_Passes.put(12, "Authenticate: Long Username Throws Validation Exception");
  }

  @Test
  void test_Authenticate_LongPassword_Throws_ValidationException() {
    // Given
    AuthenticationRequest request = AuthenticationRequest.builder()
        .username(VALID_USERNAME)
        .password(LONG_PASSWORD) // more than 50 characters
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
        "Expected to Throw ValidationException for Password Length " + MAX_PASSWORD_LENGTH + " characters");

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // pass test
    test_Passes.put(13, "Authenticate: Long Password Throws Validation Exception");
  }

  @Test
  void test_Authenticate_LongEmail_Throws_ValidationException() {
    // Given
    AuthenticationRequest request = AuthenticationRequest.builder()
        .username(LONG_EMAIL)
        .password(VALID_PASSWORD)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
        "Expected to throw ValidationException for Email Length " + MAX_EMAIL_LENGTH);

    // verify
    verifyNoInteractions(passwordEncoder, userRepository, jwtService);

    // pass the test
    test_Passes.put(14, "Authenticate: Long Email Throws Validation Exception");
  }

  /* ***************************************
   * Username and Password is Null or Empty
   * ***************************************
   */

  @Test
  void test_Register_NullOrEmptyUsername_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(NULL_OR_EMPTY_USERNAME) // null or empty username
        .password(VALID_PASSWORD)
        .email(VALID_EMAIL)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to Throw ValidationException for Null or Empty Username");

    // VERIFY
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // test passes
    test_Passes.put(15, "Register: Null Or Empty Username Throws Validation Exception");
  }

  @Test
  void test_Register_NullOrEmptyPassword_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(VALID_USERNAME)
        .password(NULL_OR_EMPTY_PASSWORD) // null or empty password
        .email(VALID_EMAIL)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to Throw ValidationException for Null or Empty Password");

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // pass test
    test_Passes.put(16, "Register: Null Or Empty Password Throws Validation Exception");
  }

  @Test
  void test_Register_NullOrEmptyEmail_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
            .username(VALID_USERNAME)
            .password(VALID_PASSWORD)
            .email(NULL_OR_EMPTY_EMAIL)
            .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
            "Expected to Throw ValidationException for Null or Empty Email");

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // pass the test
    test_Passes.put(19, "Register: Short/Null Email Throws Validation Exception");
  }

  @Test
  void test_Authenticate_NullOrEmptyUsername_Throws_ValidationException() {
    // Given
    AuthenticationRequest request = AuthenticationRequest.builder()
        .username(NULL_OR_EMPTY_USERNAME) // null or empty username
        .password(VALID_PASSWORD)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
        "Expected to Throw ValidationException for Null or Empty Username");

    // VERIFY
    verifyNoInteractions(passwordEncoder, userRepository, jwtService);

    // pass test
    test_Passes.put(17, "Authenticate: Null Or Empty Username Throws Validation Exception");
  }

  @Test
  void test_Authenticate_NullOrEmptyPassword_Throws_ValidationException() {
    // Given
    AuthenticationRequest request = AuthenticationRequest.builder()
        .username(VALID_USERNAME)
        .password(NULL_OR_EMPTY_PASSWORD) // null or empty password
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
        "Expected to Throw ValidationException for Null or Empty Password");

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // pass test
    test_Passes.put(18, "Authenticate: Null Or Empty Password Throws Validation Exception");
  }

  @Test
  void test_Authenticate_NullOrEmptyEmail_Throws_ValidationException() {
    AuthenticationRequest request = AuthenticationRequest.builder()
            .username(NULL_OR_EMPTY_EMAIL) // username can hold either username or Email
            .password(VALID_PASSWORD)
            .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
            "Expected to Throw ValidationException for Null or Empty Email");

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // pass the test
    test_Passes.put(20, "Authenticate: Null Or Empty Email Throws Validation Exception");
  }

  /******************************************
   * Main Testing Ends here
   ******************************************/

  /* Printing Test Case Passes */
  @AfterAll
  static void afterAll() {
    int maxLength = 0;
    int totalTests = 20;
    int passedTests = test_Passes.size();

    // Separate tests into Register and Authenticate groups
    Map<Integer, String> registerTests = new TreeMap<>();
    Map<Integer, String> authenticateTests = new TreeMap<>();

    for (Map.Entry<Integer, String> entry : test_Passes.entrySet()) {
      String testName = entry.getValue();
      if (testName.startsWith("Register:")) {
        registerTests.put(entry.getKey(), testName);
      } else if (testName.startsWith("Authenticate:")) {
        authenticateTests.put(entry.getKey(), testName);
      }

      // Find the longest name
      if (testName.length() > maxLength) {
        maxLength = testName.length();
      }
    }

    // Create format string
    String format = "Test %-2d: %-" + maxLength + "s -> %s%n";

    // Print results
    System.out.println("\n--- AuthenticationService Test Results ---");

    // Print Register tests
    System.out.println("\n=== REGISTER TESTS ===");
    for (Map.Entry<Integer, String> entry : registerTests.entrySet()) {
      System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
    }

    // Print Authenticate tests
    System.out.println("\n=== AUTHENTICATE TESTS ===");
    for (Map.Entry<Integer, String> entry : authenticateTests.entrySet()) {
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