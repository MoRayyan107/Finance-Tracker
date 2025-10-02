package com.rayyan.finance_tracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayyan.finance_tracker.entity.authentication.AuthenticationRequest;
import com.rayyan.finance_tracker.entity.authentication.AuthenticationResponse;
import com.rayyan.finance_tracker.entity.authentication.RegisterRequest;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.service.authentication.AuthenticationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.rayyan.finance_tracker.TestConstants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;
    
    private static final Map<Integer,String> result = new HashMap<>();



    /* ******************************** Registration Tests ******************************** */

    @Test
    void register_ValidCredentials_Success() throws Exception {
        // Given
        RegisterRequest validRegister = RegisterRequest.builder()
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .build();

        AuthenticationResponse auth = AuthenticationResponse.builder()
                .jwtToken(DUMMY_JWT_TOKEN)
                .build();

        // When
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(auth);

        // Verify
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegister)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwtToken").value(DUMMY_JWT_TOKEN));

        // Debug
        result.put(1,"Test 1 (register_ValidCredentials_Success): Pass");

        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void register_UsernameNull_Or_Empty_Fail() throws Exception {
        // Given
        RegisterRequest invalidRegister = RegisterRequest.builder()
                .username(NULL_OR_EMPTY_USERNAME) // empty/NULL username -> validationException
                .password(VALID_PASSWORD)
                .build();

        // When
        when(authenticationService.register(any(RegisterRequest.class)))
                .thenThrow(new ValidationException("Username can't be null or empty"));

        // verify
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("Username can't be null or empty")
                );

        // Debug
        result.put(2,"Test 2 (register_UsernameNull_Fail): Pass");

        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void register_UsernameTooShort_Fail() throws Exception {
        // Given
        RegisterRequest invalidRegister = RegisterRequest.builder()
                .username(SHORT_USERNAME) // Username length less than 4 -> ValidationException
                .password(VALID_PASSWORD)
                .build();

        // When
        when(authenticationService.register(any(RegisterRequest.class)))
                .thenThrow(new ValidationException("Username can't be less than 4 characters"));


        // verify
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.StatusCode").value(400))
                .andExpect(jsonPath("$.message").value("Username can't be less than 4 characters"));

        // Debug
        result.put(3,"Test 3 (register_UsernameTooShort_Fail): Pass");

        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void register_UsernameTooLong_Fail() throws Exception {
        // Given
        RegisterRequest invalidRegister = RegisterRequest.builder()
                .username(LONG_USERNAME) // username is too long -> ValidationException
                .password(VALID_PASSWORD)
                .build();

        // When
        when(authenticationService.register(any(RegisterRequest.class)))
                .thenThrow(new ValidationException("Username can't be more than 20 characters"));

        // verify
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.StatusCode").value(400),
                        jsonPath("$.message").value("Username can't be more than 20 characters")
                );

        // Debug
        result.put(4,"Test 4 (register_UsernameTooLong_Fail): Pass");

        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void register_PasswordNull_Fail() throws Exception {
        // Given
        RegisterRequest invalidRegister = RegisterRequest.builder()
                .username(VALID_USERNAME)
                .password(NULL_OR_EMPTY_PASSWORD) // empty/NULL password -> validationException
                .build();

        // When
        when(authenticationService.register(any(RegisterRequest.class)))
                .thenThrow(new ValidationException("Password can't be null or empty"));

        // verify
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("Password can't be null or empty")
                );

        // Debug
        result.put(5,"Test 5 (register_PasswordNull_Fail): Pass");

        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void register_PasswordTooShort_Fail() throws Exception {
        // Given
        RegisterRequest invalidRegister = RegisterRequest.builder()
                .username(VALID_USERNAME)
                .password(SHORT_PASSWORD) // password length is less than 8 -> Validation Exception
                .build();

        // When
        when(authenticationService.register(any(RegisterRequest.class)))
            .thenThrow(new ValidationException("Password can't be less than 8 characters"));

        // verify
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.message").value("Password can't be less than 8 characters")
                );

        // Debug
        result.put(6,"Test 6 (register_PasswordTooShort_Fail): Pass");

        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void register_PasswordTooLong_Fail() throws Exception {
        // Given
        RegisterRequest invalidRegister = RegisterRequest.builder()
                .username(VALID_USERNAME)
                .password(LONG_PASSWORD) // password is too long -> ValidationException
                .build();

        // When
        when(authenticationService.register(any(RegisterRequest.class)))
                .thenThrow(new ValidationException("Password can't be more than 50 characters"));

        // verify
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.StatusCode").value(400),
                        jsonPath("$.message").value("Password can't be more than 50 characters")
                );

        // Debug
        result.put(7,"Test 7 (register_UsernameTooLong_Fail): Pass");

        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    /* ******************************** Login Tests ******************************** */

    @Test
    void login_ValidCredentials_Success() throws Exception {
        // Given
        AuthenticationRequest validRegister = AuthenticationRequest.builder()
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .build();

        AuthenticationResponse auth = AuthenticationResponse.builder()
                .jwtToken(DUMMY_JWT_TOKEN)
                .build();

        // When
        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(auth);

        // Verify
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegister)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwtToken").value(DUMMY_JWT_TOKEN));

        // Debug
        result.put(8,"Test 8 (Login_ValidCredentials_Success): Pass");

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));

    }

    @Test
    void login_UsernameNull_Or_Empty_Fail() throws Exception {
        // Given
        AuthenticationRequest invalidRegister = AuthenticationRequest.builder()
                .username(NULL_OR_EMPTY_USERNAME) // empty/NULL username -> validationException
                .password(VALID_PASSWORD)
                .build();

        // When
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new ValidationException("Username can't be null or empty"));

        // verify
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("Username can't be null or empty")
                );

        // Debug
        result.put(9,"Test 9 (login_UsernameNull_Or_Empty_Fail): Pass");

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));

    }

    @Test
    void login_UsernameTooShort_Fail() throws Exception {
        // Given
        AuthenticationRequest invalidRegister = AuthenticationRequest.builder()
                .username(SHORT_USERNAME) // Username length less than 4 -> ValidationException
                .password(VALID_PASSWORD)
                .build();

        // When
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new ValidationException("Username can't be less than 4 characters"));


        // verify
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.StatusCode").value(400))
                .andExpect(jsonPath("$.message").value("Username can't be less than 4 characters"));

        // Debug
        result.put(10,"Test 10 (register_UsernameTooShort_Fail): Pass");

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void login_UsernameTooLong_Fail() throws Exception {
        // Given
        AuthenticationRequest invalidRegister = AuthenticationRequest.builder()
                .username(LONG_USERNAME) // username is too long -> ValidationException
                .password(VALID_PASSWORD)
                .build();

        // When
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new ValidationException("Username can't be more than 20 characters"));

        // verify
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.StatusCode").value(400),
                        jsonPath("$.message").value("Username can't be more than 20 characters")
                );

        // Debug
        result.put(11,"Test 11 (login_UsernameTooLong_Fail): Pass");

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void login_PasswordNull_Fail() throws Exception {
        // Given
        AuthenticationRequest invalidRegister = AuthenticationRequest.builder()
                .username(VALID_USERNAME)
                .password(NULL_OR_EMPTY_PASSWORD) // empty/NULL password -> validationException
                .build();

        // When
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new ValidationException("Password can't be null or empty"));

        // verify
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("Password can't be null or empty")
                );

        // Debug
        result.put(12,"Test 12 (register_PasswordNull_Fail): Pass");

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void login_PasswordTooShort_Fail() throws Exception {
        // Given
        AuthenticationRequest invalidRegister = AuthenticationRequest.builder()
                .username(VALID_USERNAME)
                .password(SHORT_PASSWORD) // password length is less than 8 -> Validation Exception
                .build();

        // When
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new ValidationException("Password can't be less than 8 characters"));

        // verify
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("Password can't be less than 8 characters")
                );

        // Debug
        result.put(13,"Test 13 (login_PasswordTooShort_Fail): Pass");

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void login_PasswordTooLong_Fail() throws Exception {
        // Given
        AuthenticationRequest invalidRegister = AuthenticationRequest.builder()
                .username(VALID_USERNAME)
                .password(LONG_PASSWORD) // password is too long -> ValidationException
                .build();

        // When
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new ValidationException("Password can't be more than 50 characters"));

        // verify
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.StatusCode").value(400),
                        jsonPath("$.message").value("Password can't be more than 50 characters")
                );

        // Debug
        result.put(14,"Test 14 (login_UsernameTooLong_Fail): Pass");

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }


    // Display of Test Passes
    @AfterAll
    static void afterAll() {
        int maxLength = 0;
        // We create a temporary list to hold just the test names
        List<String> testNames = new ArrayList<>();
        int totalTests = 14; // each register and login has 7 tests
        int passedTests = 0;

        for (String result : result.values()) {
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