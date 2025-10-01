//package com.rayyan.finance_tracker.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jayway.jsonpath.JsonPath;
//import com.rayyan.finance_tracker.config.TestConfig; // Make sure you have this test config
//import com.rayyan.finance_tracker.entity.authentication.AuthenticationResponse;
//import com.rayyan.finance_tracker.entity.authentication.RegisterRequest;
//import com.rayyan.finance_tracker.exceptions.GlobalExceptionHandler;
//import com.rayyan.finance_tracker.service.authentication.AuthenticationService;
//import com.rayyan.finance_tracker.service.jwt.JwtService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@WebMvcTest(AuthController.class)
//@Import({TestConfig.class, GlobalExceptionHandler.class})
//public class AuthControllerTest {
//
//    @MockitoBean
//    public AuthenticationService authenticationService;
//
//    @MockitoBean
//    public UserDetailsService userDetailsService;
//
//    @MockitoBean
//    private JwtService jwtService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private final String DUMMY_JWT_TOKEN = "dummy_token_for_testing_purpose";
//
//    @Test
//    void Register_ValidCredentials_Success() throws Exception {
//        // Given
//        // make a registration request -> Valid credentials
//        RegisterRequest registerRequest = RegisterRequest.builder()
//                .Username("testingUser")
//                .Password("testingPassword")
//                .build();
//
//        AuthenticationResponse expectedAuth =  AuthenticationResponse.builder()
//                .jwtToken(DUMMY_JWT_TOKEN)
//                .build();
//
//        // When
//        when(authenticationService.register(any(RegisterRequest.class)))
//                .thenReturn(expectedAuth);
//
//        // verify
//        mockMvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(registerRequest)))
//                .andExpectAll(
//                        status().isOk(),
//                        content().contentType(MediaType.APPLICATION_JSON),
//                        jsonPath("$.jwtToken").value(DUMMY_JWT_TOKEN)
//                );
//    }
//
//    @Test
//    void Register_InvalidCredentials_Fail() throws Exception {}
//
//
//}
