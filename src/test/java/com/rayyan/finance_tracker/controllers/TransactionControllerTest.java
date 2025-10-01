//package com.rayyan.finance_tracker.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rayyan.finance_tracker.config.JwtAuthenticationFilter;
//import com.rayyan.finance_tracker.config.TestConfig;
//import com.rayyan.finance_tracker.entity.Transaction;
//import com.rayyan.finance_tracker.entity.User;
//import com.rayyan.finance_tracker.exceptions.GlobalExceptionHandler;
//import com.rayyan.finance_tracker.exceptions.TransactionNotFoundException;
//import com.rayyan.finance_tracker.exceptions.ValidationException;
//import com.rayyan.finance_tracker.service.TransactionService;
//import com.rayyan.finance_tracker.service.jwt.JwtService;
//import com.rayyan.finance_tracker.service.UserDetailService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(TransactionController.class)
//@Import({TestConfig.class, GlobalExceptionHandler.class})
//public class TransactionControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private TransactionService transactionService;
//
//    @MockitoBean
//    private JwtService jwtService;
//
//    @MockitoBean
//    private UserDetailService userDetailService;
//
//    @MockitoBean
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    private static final LocalDateTime TEST_DATE = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
//
//    private Transaction validTransaction;
//    private Transaction invalidTransaction;
//    private Transaction updatedTransaction;
//    private User testUser;
//
//    @BeforeEach
//    void setup() {
//        // Create test user
//        testUser = User.builder()
//                .id(1L)
//                .username("testuser")
//                .password("password")
//                .role(User.Role.USER)
//                .build();
//
//        // Set up security context with mock user
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Mock UserDetailService to return test user
//        when(userDetailService.getUserByUsername(anyString())).thenReturn(testUser);
//
//        // Valid Transaction
//        validTransaction = new Transaction();
//        validTransaction.setId(1L);
//        validTransaction.setDescription("Test Shopping Transaction");
//        validTransaction.setAmount(new BigDecimal("3000.00"));
//        validTransaction.setTransactionType(Transaction.TransactionType.EXPENSE);
//        validTransaction.setCategory("Shopping");
//        validTransaction.setDate(TEST_DATE);
//        validTransaction.setUser(testUser);
//
//        // Invalid Transaction (missing type)
//        invalidTransaction = new Transaction();
//        invalidTransaction.setDescription("Test Shopping Transaction");
//        invalidTransaction.setAmount(new BigDecimal("3000.00"));
//        invalidTransaction.setCategory("Shopping");
//        invalidTransaction.setDate(TEST_DATE);
//        invalidTransaction.setTransactionType(null);
//
//        // Updated Transaction
//        updatedTransaction = new Transaction();
//        updatedTransaction.setDescription("Updated Shopping Transaction");
//        updatedTransaction.setAmount(new BigDecimal("5000.00"));
//        updatedTransaction.setTransactionType(Transaction.TransactionType.EXPENSE);
//        updatedTransaction.setCategory("Electronics");
//        updatedTransaction.setDate(TEST_DATE);
//    }
//
//    /* ******************** CREATE Transaction Tests ******************** */
//    @Test
//    void transactionCreation_ValidBody_Success() throws Exception {
//        doNothing().when(transactionService).createTransaction(any(Transaction.class));
//
//        mockMvc.perform(post("/api/transaction/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(validTransaction)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Transaction Successfully created"));
//
//        verify(transactionService, times(1)).createTransaction(any(Transaction.class));
//    }
//
//    @Test
//    void transactionCreation_InvalidBody_Fail() throws Exception {
//        doThrow(new ValidationException("Transaction Type cannot be empty"))
//                .when(transactionService).createTransaction(any(Transaction.class));
//
//        mockMvc.perform(post("/api/transaction/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidTransaction)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Transaction Type cannot be empty"))
//                .andExpect(jsonPath("$.StatusCode").value(400));
//    }
//
//    /* ******************** GET All Transactions Tests ******************** */
//    @Test
//    void getAllTransactions_Existing_Transaction() throws Exception {
//        when(transactionService.findTransactionsByUser(any(User.class)))
//                .thenReturn(List.of(validTransaction));
//
//        mockMvc.perform(get("/api/transaction/fetchAll"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$[0].description").value("Test Shopping Transaction"))
//                .andExpect(jsonPath("$[0].amount").value(3000.00));
//
//        verify(transactionService, times(1)).findTransactionsByUser(any(User.class));
//    }
//
//    @Test
//    void getAllTransactions_No_Transaction_Exists() throws Exception {
//        when(transactionService.findTransactionsByUser(any(User.class)))
//                .thenReturn(Collections.emptyList());
//
//        mockMvc.perform(get("/api/transaction/fetchAll"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isEmpty());
//
//        verify(transactionService, times(1)).findTransactionsByUser(any(User.class));
//    }
//
//    /* ******************** GET Transaction by ID Tests ******************** */
//    @Test
//    void getTransactionById_ReturnTransaction_WhenFound() throws Exception {
//        when(transactionService.getTransactionByIdAndUser(eq(1L), any(User.class)))
//                .thenReturn(validTransaction);
//
//        mockMvc.perform(get("/api/transaction/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.description").value("Test Shopping Transaction"))
//                .andExpect(jsonPath("$.category").value("Shopping"));
//
//        verify(transactionService, times(1)).getTransactionByIdAndUser(eq(1L), any(User.class));
//    }
//
//    @Test
//    void getTransactionById_ReturnError_WhenNotFound() throws Exception {
//        doThrow(new TransactionNotFoundException("Transaction Not Found"))
//                .when(transactionService).getTransactionByIdAndUser(anyLong(), any(User.class));
//
//        mockMvc.perform(get("/api/transaction/7682"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("Transaction Not Found"))
//                .andExpect(jsonPath("$.StatusCode").value(404));
//
//        verify(transactionService, times(1)).getTransactionByIdAndUser(eq(7682L), any(User.class));
//    }
//
//    /* ******************** UPDATE Transaction Tests ******************** */
//    @Test
//    void updateTransaction_ValidId_ReturnSuccess_Message() throws Exception {
//        Long ID = 1L;
//        when(transactionService.updateTransaction(eq(ID), any(Transaction.class), any(User.class)))
//                .thenReturn("Transaction updated with ID: " + ID);
//
//        mockMvc.perform(put("/api/transaction/update/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedTransaction)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Transaction updated with ID: 1"));
//
//        verify(transactionService, times(1))
//                .updateTransaction(eq(ID), any(Transaction.class), any(User.class));
//    }
//
//    @Test
//    void updateTransaction_InvalidId_ReturnError() throws Exception {
//        Long ID = 999L;
//        doThrow(new TransactionNotFoundException("Transaction Not Found"))
//                .when(transactionService).updateTransaction(eq(ID), any(Transaction.class), any(User.class));
//
//        mockMvc.perform(put("/api/transaction/update/999")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedTransaction)))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("Transaction Not Found"))
//                .andExpect(jsonPath("$.StatusCode").value(404));
//    }
//
//    @Test
//    void updateTransaction_ValidId_InvalidBody_ReturnError() throws Exception {
//        Long ID = 1L;
//        doThrow(new ValidationException("Transaction Type cannot be empty"))
//                .when(transactionService).updateTransaction(eq(ID), any(Transaction.class), any(User.class));
//
//        mockMvc.perform(put("/api/transaction/update/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidTransaction)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Transaction Type cannot be empty"))
//                .andExpect(jsonPath("$.StatusCode").value(400));
//    }
//
//    /* ******************** DELETE Transaction Tests ******************** */
//    @Test
//    void deleteTransaction_ValidId_ReturnSuccess_Message() throws Exception {
//        Long ID = 1L;
//        when(transactionService.deleteTransaction(eq(ID), any(User.class)))
//                .thenReturn("Transaction deleted with ID: " + ID);
//
//        mockMvc.perform(delete("/api/transaction/delete/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Transaction deleted with ID: 1"));
//
//        verify(transactionService, times(1)).deleteTransaction(eq(ID), any(User.class));
//    }
//
//    @Test
//    void deleteTransaction_InvalidId_ReturnError() throws Exception {
//        Long ID = 999L;
//        doThrow(new TransactionNotFoundException("Transaction Not Found"))
//                .when(transactionService).deleteTransaction(eq(ID), any(User.class));
//
//        mockMvc.perform(delete("/api/transaction/delete/999"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("Transaction Not Found"))
//                .andExpect(jsonPath("$.StatusCode").value(404));
//    }
//}