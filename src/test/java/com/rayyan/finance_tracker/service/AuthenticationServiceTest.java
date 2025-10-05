package com.rayyan.finance_tracker.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.repository.UserRepository;
import com.rayyan.finance_tracker.service.authentication.AuthenticationService;
import com.rayyan.finance_tracker.service.jwt.JwtService;

import static com.rayyan.finance_tracker.TestConstants.*;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

  private static final Map<Integer,String> test_Passes = new HashMap<>();

  @BeforeAll
  static void beforeAll() {
    System.out.println("\n--- Starting Authentication Service Tests ---");
  }

  @BeforeEach
  void setUp() {
    validRegisterRequest = RegisterRequest.builder()
       .username(VALID_USERNAME)
       .password(VALID_PASSWORD)
       .build();

    validAuthenticationRequest = AuthenticationRequest.builder()
        .username(VALID_USERNAME)
        .password(VALID_PASSWORD)
        .build();
    
    user = User.builder()
       .username(VALID_USERNAME)
       .password(TEST_ENCODED_PASSWORD)
       .role(User.Role.USER)
       .build();
  }

  /* ********************* Valid Username and Password ********************* */
  @Test
  void test_Register_ValidCredentials_Success(){
    // mockito when-then
    when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(jwtService.generateToken(any(User.class))).thenReturn(DUMMY_JWT_TOKEN);

    //Given 
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
    assertEquals(TEST_ENCODED_PASSWORD, capturedUser.getPassword(), "Password should be encoded");
    assertEquals(User.Role.USER, capturedUser.getRole(), "Role should be USER");

    // verify that the JWT service was called once
    verify(jwtService, times(1)).generateToken(capturedUser);

    // test passes
    test_Passes.put(1, "Test 1: (test_Register_ValidCredentials_Success): PASS");
  }

  @Test
  void test_Authenticate_ValidCredentials_Success(){
    //Given
    when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(user));
    when(jwtService.generateToken(any(User.class))).thenReturn(DUMMY_JWT_SECRET_KEY);

    // mock the authenticationManager to do nothing (since it returns void)
    // this is to simulate a successful authentication
    // if authentication fails, it would throw an exception
    // so we don't need to do anything special here for a successful case
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
    test_Passes.put(2, "Test 2: (test_Authenticate_ValidCredentials_Success): PASS");
  }

  /* ********************* Username and Password is Short ********************* */
  @Test
  void test_Register_ShortUsername_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(SHORT_USERNAME) // less than 4 characters
        .password(VALID_PASSWORD)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to Throw ValidationException for Username Length "+MIN_USERNAME_LENGTH+" characters");

    // VERIFY
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);
    
    // test passes
    test_Passes.put(3, "Test 3: (test_Register_ShortUsername_Throws_ValidationException): PASS");
  }

  @Test
  void test_Register_ShortPassword_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(VALID_USERNAME)
        .password(SHORT_PASSWORD) // less than 8 characters
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request)
        ,"Expected to Throw ValidationException for Password Length "+MIN_PASSWORD_LENGTH+" characters"
    );

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // test passes
    test_Passes.put(4, "Test 4: (test_Register_ShortPassword_Throws_ValidationException): PASS");
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
        "Expected to Throw ValidationException for Username Length "+MIN_USERNAME_LENGTH+" characters");

    // VERIFY
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);
    
    // test passes
    test_Passes.put(5, "Test 5: (test_login_ShortUsername_Throws_ValidationException): PASS");
  }

  @Test
  void test_Authenticate_ShortPassword_Throws_ValidationException(){
    // Given 
    AuthenticationRequest request = AuthenticationRequest.builder()
        .username(VALID_USERNAME)
        .password(SHORT_PASSWORD) // less than 8 characters
        .build();
        
    // assert 
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
        "Expected to Throw ValidationException for Password Length "+MIN_PASSWORD_LENGTH+" characters");

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // test passes
    test_Passes.put(6, "Test 6: (test_Authenticate_ShortPassword_Throws_ValidationException): PASS");
  }
  
  /* ********************* Username and Password is Long ********************* */
  @Test
  void test_Register_LongUsername_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(LONG_USERNAME) // more than 20 characters
        .password(VALID_PASSWORD)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to Throw ValidationException for Username Length "+MAX_USERNAME_LENGTH+" characters");

    // VERIFY
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);
    
    // test passes
    test_Passes.put(7, "Test 7: (test_Register_LongUsername_Throws_ValidationException): PASS");
  }

  @Test
  void test_Register_LongPassword_Throws_ValidationException(){
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(VALID_USERNAME)
        .password(LONG_PASSWORD) // more than 50 characters
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to Throw ValidationException for Password Length "+MAX_PASSWORD_LENGTH+" characters");

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // pass test
    test_Passes.put(8, "Test 8: (test_Register_LongPassword_Throws_ValidationException): PASS");
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
        "Expected to Throw ValidationException for Username Length "+MAX_USERNAME_LENGTH+" characters");
    
    // VERIFY
    verifyNoInteractions(passwordEncoder, userRepository, jwtService);

    // pass test
    test_Passes.put(9, "Test 9: (test_Authenticate_LongUsername_Throws_ValidationException): PASS");
  }

  @Test
  void test_Authenticate_LongPassword_Throws_ValidationException(){
    // Given
    AuthenticationRequest request = AuthenticationRequest.builder()
        .username(VALID_USERNAME)
        .password(LONG_PASSWORD) // more than 50 characters
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
        "Expected to Throw ValidationException for Password Length "+MAX_PASSWORD_LENGTH+" characters"
    );

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // pass test
    test_Passes.put(10, "Test 10: (test_Authenticate_LongPassword_Throws_ValidationException): PASS");
  }

  /* ********************* Username and Password is Null or Empty ********************* */
  @Test
  void test_Register_NullOrEmptyUsername_Throws_ValidationException() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(NULL_OR_EMPTY_USERNAME) // null or empty username
        .password(VALID_PASSWORD)
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to Throw ValidationException for Null or Empty Username");

    // VERIFY
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);
    
    // test passes
    test_Passes.put(11, "Test 11: (test_Register_NullOrEmptyUsername_Throws_ValidationException): PASS");
  }

  @Test
  void test_Register_NullOrEmptyPassword_Throws_ValidationException(){
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username(VALID_USERNAME)
        .password(NULL_OR_EMPTY_PASSWORD) // null or empty password
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.register(request),
        "Expected to Throw ValidationException for Null or Empty Password");

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // pass test
    test_Passes.put(12, "Test 12: (test_Register_NullOrEmptyPassword_Throws_ValidationException): PASS");
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
    test_Passes.put(13, "Test 13: (test_Authenticate_NullOrEmptyUsername_Throws_ValidationException): PASS");
  }
  
  @Test
  void test_Authenticate_NullOrEmptyPassword_Throws_ValidationException(){
    // Given
    AuthenticationRequest request = AuthenticationRequest.builder()
        .username(VALID_USERNAME)
        .password(NULL_OR_EMPTY_PASSWORD) // null or empty password
        .build();

    // assert
    assertThrows(ValidationException.class, () -> authService.authenticate(request),
        "Expected to Throw ValidationException for Null or Empty Password"
    );

    // verify
    verifyNoInteractions(userRepository, passwordEncoder, jwtService);

    // pass test
    test_Passes.put(14, "Test 14: (test_Authenticate_NullOrEmptyPassword_Throws_ValidationException): PASS");
  }

/****************************************** Main Testing Ends here  ******************************************/
  /* Printing Test Case Passes */
  @AfterAll
  static void afterAll() {
    int maxLength = 0;
    // We create a temporary list to hold just the test names
    List<String> testNames = new ArrayList<>();
    int totalTests = 14; // each Register and Authenticate has 7 tests
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
    System.out.println("\n--- AuthenticationService Test Results ---");
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
