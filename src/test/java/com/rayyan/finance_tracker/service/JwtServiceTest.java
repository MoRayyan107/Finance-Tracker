package com.rayyan.finance_tracker.service;

import static com.rayyan.finance_tracker.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.service.jwt.JwtService;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

  @InjectMocks
  private JwtService jwtService;

  private UserDetails userDetails;
  private static final Map<Integer, String> test_Passes = new HashMap<>();

  @BeforeAll
  static void beforeAll() {
    System.out.println("\n--- Starting JWT Service Tests ---");
  }

  @BeforeEach
  void setup() {
    userDetails = User.builder()
            .username(VALID_USERNAME)
            .password(VALID_PASSWORD)
            .email(VALID_EMAIL)
            .role(User.Role.USER)
            .build();

    ReflectionTestUtils.setField(jwtService, "SECRET_KEY", TEST_SECRET_KEY);
  }

  /* **************** Token Generation Test **************** */
  @Test
  void test_TokenGeneration_ShouldBe_Successful() {
    String token = jwtService.generateToken(userDetails);

    assertNotNull(token);
    assertFalse(token.isEmpty());
    assertEquals(3, token.split("\\.").length);

    test_Passes.put(1, "Generate: generate token With User Details");
  }

  @Test
  void test_GenerateToken_ExtraClaim_ShouldBe_Successful() {
    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("ExtraClam", "ExtraValue");

    String token = jwtService.generateToken(extraClaims,userDetails);

    assertNotNull(token);
    assertFalse(token.isEmpty());

    test_Passes.put(2, "Generate: generate Token based on Claims and user details");
  }

  @Test
  void test_GeneratedToken_Is_Different_For_Different_User() {
    var userDetails2 = User.builder()
            .username("differentUsername")
            .password("<PASSWORD>")
            .email("<EMAIL>")
            .role(User.Role.USER)
            .build();

    String token1 = jwtService.generateToken(userDetails);
    String token2 = jwtService.generateToken(userDetails2);

    assertNotEquals(token1, token2);

    test_Passes.put(8, "Generate: generate Token based on Claims and user details");
  }

  @Test
  void test_GeneratedToken_Is_Different_For_Different_User_With_ExtraClaim() {
    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("ExtraClam", "ExtraValue");

    var userDetails2 = User.builder()
            .username("differentUsername")
            .password("<PASSWORD>")
            .email("<EMAIL>")
            .role(User.Role.USER)
            .build();

    String token1 = jwtService.generateToken(extraClaims,userDetails);
    String token2 = jwtService.generateToken(extraClaims,userDetails2);

    assertNotEquals(token1, token2);

    test_Passes.put(9, "Generate: generate Token based on Claims and user details");
  }

  @Test
  void test_GeneratedToken_Is_Different_For_Different_User_With_Different_Claims() {
    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("ExtraClam", "ExtraValue");
    extraClaims.put("ExtraClam2", "ExtraValue2");

    var userDetails2 = User.builder()
            .username("differentUsername")
            .password("<PASSWORD>")
            .email("<EMAIL>")
            .role(User.Role.USER)
            .build();

    String tokrn1 = jwtService.generateToken(extraClaims,userDetails);
    String token2 = jwtService.generateToken(extraClaims,userDetails2);

    assertNotEquals(tokrn1, token2);

    test_Passes.put(10, "Generate: generate Token based on Claims and user details");
  }

  /* **************** Token Validity Check **************** */
  @Test
  void test_IsTokenValid_ValidToken_Returns_True(){
    String token = jwtService.generateToken(userDetails);

    assertTrue(jwtService.isTokenValid(token, userDetails));

    test_Passes.put(3, "Validate: validation of Token MUST return True");
  }

  @Test
  void test_isTokenValid_WrongUser_Returns_False(){
    String token = jwtService.generateToken(userDetails);

    var userDetails = User.builder()
            .username("wrongUsername123")
            .password("-password-123")
            .email("wrong@email.com")
            .build();

    assertFalse(jwtService.isTokenValid(token, userDetails));

    test_Passes.put(4, "Validate: validation of Token MUST return False");
  }

  @Test
  void test_isTokenValid_ExpiredToken_Returns_False() {
    // Define clear time intervals
    long expirationTime = System.currentTimeMillis() - 10000; // 10 seconds ago
    long issuedAtTime = expirationTime - 2000; // 2 seconds before expiration
    
    String expiredToken = Jwts.builder()
            .setSubject(VALID_USERNAME)
            .setIssuedAt(new Date(issuedAtTime))
            .setExpiration(new Date(expirationTime))
            .signWith(Keys.hmacShaKeyFor(
                    io.jsonwebtoken.io.Decoders.BASE64.decode(TEST_SECRET_KEY)
            ))
            .compact();

    boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);
    assertFalse(isValid);
    test_Passes.put(5, "Validate: validation of Expired Token MUST return False");
}

  /* **************** Username Extraction Test **************** */
  @Test
  void test_ExtractUsername_ValidToken_Returns_Username(){
    String token = jwtService.generateToken(userDetails);

    String username = jwtService.extractUsername(token);

    assertEquals(VALID_USERNAME, username);

    test_Passes.put(6, "Extract: extraction of Username MUST return Username");
  }

  /* **************** Token Structure Test **************** */
  @Test
  void test_GenerateToken_ValidStructure_Returns_True(){
    String token = jwtService.generateToken(userDetails);

    int length = token.split("\\.").length;
    String extractUsername = jwtService.extractUsername(token);

    assertEquals(VALID_USERNAME, extractUsername);
    assertEquals(3, length);

    test_Passes.put(7, "Structure: token MUST have 3 parts and extractUsername MUST return Username");
  }

  @AfterAll
  static void afterAll() {
    int maxLength = 0;
    int totalTests = 8;
    int passedTests = test_Passes.size();

    Map<Integer, String> generateTests = new TreeMap<>();
    Map<Integer, String> extractTests = new TreeMap<>();
    Map<Integer, String> validateTests = new TreeMap<>();
    Map<Integer, String> structureTests = new TreeMap<>();

    for (Map.Entry<Integer, String> entry : test_Passes.entrySet()) {
      String testName = entry.getValue();
      if (testName.startsWith("Generate:")) {
        generateTests.put(entry.getKey(), testName);
      } else if (testName.startsWith("Extract:")) {
        extractTests.put(entry.getKey(), testName);
      } else if (testName.startsWith("Validate:")) {
        validateTests.put(entry.getKey(), testName);
      } else if (testName.startsWith("Structure:")) {
        structureTests.put(entry.getKey(), testName);
      }

      if (testName.length() > maxLength) {
        maxLength = testName.length();
      }
    }

    String format = "Test %-2d: %-" + maxLength + "s -> %s%n";

    System.out.println("\n--- JwtService Test Results ---");

    if (!generateTests.isEmpty()) {
      System.out.println("\n=== GENERATE TESTS ===");
      for (Map.Entry<Integer, String> entry : generateTests.entrySet()) {
        System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
      }
    }

    if (!extractTests.isEmpty()) {
      System.out.println("\n=== EXTRACT TESTS ===");
      for (Map.Entry<Integer, String> entry : extractTests.entrySet()) {
        System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
      }
    }

    if (!validateTests.isEmpty()) {
      System.out.println("\n=== VALIDATE TESTS ===");
      for (Map.Entry<Integer, String> entry : validateTests.entrySet()) {
        System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
      }
    }

    if (!structureTests.isEmpty()) {
      System.out.println("\n=== STRUCTURE TESTS ===");
      for (Map.Entry<Integer, String> entry : structureTests.entrySet()) {
        System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
      }
    }

    System.out.println("\n---------------------------------");

    if (passedTests == totalTests)
      System.out.println("SUMMARY: All " + passedTests + "/" + totalTests + " tests passed!");
    else
      System.out.println("SUMMARY: " + passedTests + "/" + totalTests + " tests passed.");
    System.out.println("---------------------------------");
  }

}