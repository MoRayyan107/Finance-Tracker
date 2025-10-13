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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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

        private static final Map<Integer, String> test_Passes = new HashMap<>();

        @BeforeAll
        static void beforeAll() {
                System.out.println("\n--- Starting AuthController Tests ---");
        }

        /*
         * ******************************** Registration Tests
         * ********************************
         */

        @Test
        void register_ValidCredentials_Success() throws Exception {
                RegisterRequest validRegister = RegisterRequest.builder()
                                .username(VALID_USERNAME)
                                .password(VALID_PASSWORD)
                                .build();

                AuthenticationResponse auth = AuthenticationResponse.builder()
                                .jwtToken(DUMMY_JWT_TOKEN)
                                .build();

                when(authenticationService.register(any(RegisterRequest.class))).thenReturn(auth);

                mockMvc.perform(post(REGISTER_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRegister)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.jwtToken").value(DUMMY_JWT_TOKEN));

                test_Passes.put(1, "Register: Valid Credentials Success");

                verify(authenticationService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        void register_UsernameNull_Or_Empty_Fail() throws Exception {
                RegisterRequest invalidRegister = RegisterRequest.builder()
                                .username(NULL_OR_EMPTY_USERNAME)
                                .password(VALID_PASSWORD)
                                .build();

                when(authenticationService.register(any(RegisterRequest.class)))
                                .thenThrow(new ValidationException("Username can't be null or empty"));

                mockMvc.perform(post(REGISTER_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRegister)))
                                .andExpectAll(
                                                status().isBadRequest(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$.message").value("Username can't be null or empty"));

                test_Passes.put(2, "Register: Username Null Or Empty Throws Exception");

                verify(authenticationService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        void register_UsernameTooShort_Fail() throws Exception {
                RegisterRequest invalidRegister = RegisterRequest.builder()
                                .username(SHORT_USERNAME)
                                .password(VALID_PASSWORD)
                                .build();

                when(authenticationService.register(any(RegisterRequest.class)))
                                .thenThrow(new ValidationException("Username can't be less than 4 characters"));

                mockMvc.perform(post(REGISTER_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRegister)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.StatusCode").value(400))
                                .andExpect(jsonPath("$.message").value("Username can't be less than 4 characters"));

                test_Passes.put(3, "Register: Username Too Short Throws Exception");

                verify(authenticationService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        void register_UsernameTooLong_Fail() throws Exception {
                RegisterRequest invalidRegister = RegisterRequest.builder()
                                .username(LONG_USERNAME)
                                .password(VALID_PASSWORD)
                                .build();

                when(authenticationService.register(any(RegisterRequest.class)))
                                .thenThrow(new ValidationException("Username can't be more than 20 characters"));

                mockMvc.perform(post(REGISTER_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRegister)))
                                .andExpectAll(
                                                status().isBadRequest(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$.StatusCode").value(400),
                                                jsonPath("$.message")
                                                                .value("Username can't be more than 20 characters"));

                test_Passes.put(4, "Register: Username Too Long Throws Exception");

                verify(authenticationService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        void register_PasswordNull_Fail() throws Exception {
                RegisterRequest invalidRegister = RegisterRequest.builder()
                                .username(VALID_USERNAME)
                                .password(NULL_OR_EMPTY_PASSWORD)
                                .build();

                when(authenticationService.register(any(RegisterRequest.class)))
                                .thenThrow(new ValidationException("Password can't be null or empty"));

                mockMvc.perform(post(REGISTER_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRegister)))
                                .andExpectAll(
                                                status().isBadRequest(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$.message").value("Password can't be null or empty"));

                test_Passes.put(5, "Register: Password Null Throws Exception");

                verify(authenticationService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        void register_PasswordTooShort_Fail() throws Exception {
                RegisterRequest invalidRegister = RegisterRequest.builder()
                                .username(VALID_USERNAME)
                                .password(SHORT_PASSWORD)
                                .build();

                when(authenticationService.register(any(RegisterRequest.class)))
                                .thenThrow(new ValidationException("Password can't be less than 8 characters"));

                mockMvc.perform(post(REGISTER_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRegister)))
                                .andExpectAll(
                                                status().isBadRequest(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$.message")
                                                                .value("Password can't be less than 8 characters"));

                test_Passes.put(6, "Register: Password Too Short Throws Exception");

                verify(authenticationService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        void register_PasswordTooLong_Fail() throws Exception {
                RegisterRequest invalidRegister = RegisterRequest.builder()
                                .username(VALID_USERNAME)
                                .password(LONG_PASSWORD)
                                .build();

                when(authenticationService.register(any(RegisterRequest.class)))
                                .thenThrow(new ValidationException("Password can't be more than 50 characters"));

                mockMvc.perform(post(REGISTER_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRegister)))
                                .andExpectAll(
                                                status().isBadRequest(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$.StatusCode").value(400),
                                                jsonPath("$.message")
                                                                .value("Password can't be more than 50 characters"));

                test_Passes.put(7, "Register: Password Too Long Throws Exception");

                verify(authenticationService, times(1)).register(any(RegisterRequest.class));
        }

        /*
         * ******************************** Login Tests ********************************
         */

        @Test
        void login_ValidCredentials_Success() throws Exception {
                AuthenticationRequest validLogin = AuthenticationRequest.builder()
                                .username(VALID_USERNAME)
                                .password(VALID_PASSWORD)
                                .build();

                AuthenticationResponse auth = AuthenticationResponse.builder()
                                .jwtToken(DUMMY_JWT_TOKEN)
                                .build();

                when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(auth);

                mockMvc.perform(post(LOGIN_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validLogin)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.jwtToken").value(DUMMY_JWT_TOKEN));

                test_Passes.put(8, "Login: Valid Credentials Success");

                verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
        }

        @Test
        void login_UsernameNull_Or_Empty_Fail() throws Exception {
                AuthenticationRequest invalidLogin = AuthenticationRequest.builder()
                                .username(NULL_OR_EMPTY_USERNAME)
                                .password(VALID_PASSWORD)
                                .build();

                when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                                .thenThrow(new ValidationException("Username can't be null or empty"));

                mockMvc.perform(post(LOGIN_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidLogin)))
                                .andExpectAll(
                                                status().isBadRequest(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$.message").value("Username can't be null or empty"));

                test_Passes.put(9, "Login: Username Null Or Empty Throws Exception");

                verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
        }

        @Test
        void login_UsernameTooShort_Fail() throws Exception {
                AuthenticationRequest invalidLogin = AuthenticationRequest.builder()
                                .username(SHORT_USERNAME)
                                .password(VALID_PASSWORD)
                                .build();

                when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                                .thenThrow(new ValidationException("Username can't be less than 4 characters"));

                mockMvc.perform(post(LOGIN_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidLogin)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.StatusCode").value(400))
                                .andExpect(jsonPath("$.message").value("Username can't be less than 4 characters"));

                test_Passes.put(10, "Login: Username Too Short Throws Exception");

                verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
        }

        @Test
        void login_UsernameTooLong_Fail() throws Exception {
                AuthenticationRequest invalidLogin = AuthenticationRequest.builder()
                                .username(LONG_USERNAME)
                                .password(VALID_PASSWORD)
                                .build();

                when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                                .thenThrow(new ValidationException("Username can't be more than 20 characters"));

                mockMvc.perform(post(LOGIN_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidLogin)))
                                .andExpectAll(
                                                status().isBadRequest(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$.StatusCode").value(400),
                                                jsonPath("$.message")
                                                                .value("Username can't be more than 20 characters"));

                test_Passes.put(11, "Login: Username Too Long Throws Exception");

                verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
        }

        @Test
        void login_PasswordNull_Fail() throws Exception {
                AuthenticationRequest invalidLogin = AuthenticationRequest.builder()
                                .username(VALID_USERNAME)
                                .password(NULL_OR_EMPTY_PASSWORD)
                                .build();

                when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                                .thenThrow(new ValidationException("Password can't be null or empty"));

                mockMvc.perform(post(LOGIN_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidLogin)))
                                .andExpectAll(
                                                status().isBadRequest(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$.message").value("Password can't be null or empty"));

                test_Passes.put(12, "Login: Password Null Throws Exception");

                verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
        }

        @Test
        void login_PasswordTooShort_Fail() throws Exception {
                AuthenticationRequest invalidLogin = AuthenticationRequest.builder()
                                .username(VALID_USERNAME)
                                .password(SHORT_PASSWORD)
                                .build();

                when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                                .thenThrow(new ValidationException("Password can't be less than 8 characters"));

                mockMvc.perform(post(LOGIN_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidLogin)))
                                .andExpectAll(
                                                status().isBadRequest(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$.message")
                                                                .value("Password can't be less than 8 characters"));

                test_Passes.put(13, "Login: Password Too Short Throws Exception");

                verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
        }

        @Test
        void login_PasswordTooLong_Fail() throws Exception {
                AuthenticationRequest invalidLogin = AuthenticationRequest.builder()
                                .username(VALID_USERNAME)
                                .password(LONG_PASSWORD)
                                .build();

                when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                                .thenThrow(new ValidationException("Password can't be more than 50 characters"));

                mockMvc.perform(post(LOGIN_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidLogin)))
                                .andExpectAll(
                                                status().isBadRequest(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$.StatusCode").value(400),
                                                jsonPath("$.message")
                                                                .value("Password can't be more than 50 characters"));

                test_Passes.put(14, "Login: Password Too Long Throws Exception");

                verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
        }

        @AfterAll
        static void afterAll() {
                int maxLength = 0;
                int totalTests = 14;
                int passedTests = test_Passes.size();

                Map<Integer, String> registerTests = new TreeMap<>();
                Map<Integer, String> loginTests = new TreeMap<>();

                for (Map.Entry<Integer, String> entry : test_Passes.entrySet()) {
                        String testName = entry.getValue();
                        if (testName.startsWith("Register:")) {
                                registerTests.put(entry.getKey(), testName);
                        } else if (testName.startsWith("Login:")) {
                                loginTests.put(entry.getKey(), testName);
                        }

                        if (testName.length() > maxLength) {
                                maxLength = testName.length();
                        }
                }

                String format = "Test %-2d: %-" + maxLength + "s -> %s%n";

                System.out.println("\n--- AuthController Test Results ---");

                System.out.println("\n=== REGISTER TESTS ===");
                for (Map.Entry<Integer, String> entry : registerTests.entrySet()) {
                        System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
                }

                System.out.println("\n=== LOGIN TESTS ===");
                for (Map.Entry<Integer, String> entry : loginTests.entrySet()) {
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