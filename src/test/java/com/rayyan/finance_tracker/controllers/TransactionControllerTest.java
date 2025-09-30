package com.rayyan.finance_tracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayyan.finance_tracker.config.ApplicationConfig;
import com.rayyan.finance_tracker.config.JwtAuthenticationFilter;
import com.rayyan.finance_tracker.entity.Transaction;
import com.rayyan.finance_tracker.exceptions.TransactionNotFoundException;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.service.TransactionService;
import com.rayyan.finance_tracker.service.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class) // Tests only the controller
@WithMockUser                            // -< Runs as authenticated User
public class TransactionControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc; // Stimulates HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // converts object to JSON format

    @MockitoBean
    private TransactionService transactionService; // Mocks the service layer

    private static final LocalDateTime TEST_DATE = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    private Transaction ValidTransaction;
    private Transaction InvalidTransaction;

    @BeforeEach
    void setup() {
        /* ********** Valid Transaction *********** */
        ValidTransaction = new Transaction();
        ValidTransaction.setId(1L);
        ValidTransaction.setDescription("Test Shopping Transaction");
        ValidTransaction.setAmount(new BigDecimal("3000.00"));
        ValidTransaction.setTransactionType(Transaction.TransactionType.EXPENSE);
        ValidTransaction.setCategory("Shopping");
        ValidTransaction.setDate(TEST_DATE);

        /* ********** Invalid Transaction *********** */
        InvalidTransaction = new Transaction();
        InvalidTransaction.setDescription("Test Shopping Transaction");
        InvalidTransaction.setAmount(new BigDecimal("3000.00"));
        InvalidTransaction.setCategory("Shopping");
        InvalidTransaction.setDate(TEST_DATE);

        // this will fail's the validation of transaction
        InvalidTransaction.setTransactionType(null);
    }

    /* ******************** CREATE Transaction Tests ******************** */
    @Test
    void transactionCreation_ValidBody_Success() throws Exception {
        // mock a service layer
        doNothing().when(transactionService).createTransaction(any(Transaction.class));

        // make a post on API
        mockMvc.perform(post("/api/transaction/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ValidTransaction)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction Successfully created"));
    }

    @Test
    void TransactionCreation_InvalidBody_Fail() throws Exception {
        // mock the service layer
        // check the line block "InvalidTransactionCreation_To_JSON"
        doThrow(new ValidationException("Transaction Type cannot be empty"))
                .when(transactionService).createTransaction(any(Transaction.class));

        // make an invalid transaction
        mockMvc.perform(post("/api/transaction/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(InvalidTransaction)))
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value("Transaction Type cannot be empty"),
                        jsonPath("$.StatusCode").value(400)
                );
    }

    /* ******************** GET Transaction Tests ******************** */
    @Test
    void getAllTransactions_Existing_Transaction() throws Exception {
        // mock the service layer
        when(transactionService.findAllTransactions()).thenReturn(List.of(ValidTransaction));

        // make an fetch transactions
        mockMvc.perform(get("/api/transaction/fetchAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description").value("Test Shopping Transaction"));
    }


    @Test
    void getAllTransactions_No_Transaction_Exists() throws Exception {
        // mock the service layer, returns an empty list
        when(transactionService.findAllTransactions()).thenReturn(Collections.emptyList());

        // make a fetch transaction
        mockMvc.perform(get("/api/transaction/fetchAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    /* ******************** GET Transaction Tests ******************** */
    @Test
    void getTransactionById_ReturnTransaction_WhenFound() throws Exception {
        // set the id of valid transaction
        Long ID = 1L;
        ValidTransaction.setId(ID);
        // mock the service layer
        when(transactionService.getTransaction(ID)).thenReturn(ValidTransaction);

        // make a fetch transaction for specific id
        mockMvc.perform(get("/api/transaction/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.description").value("Test Shopping Transaction"));
    }

    @Test
    void getTransactionById_ReturnError_WhenNotFound() throws Exception {
        // mcok the service layer, throw TransactionNotFound Exception
        doThrow(new TransactionNotFoundException("Transaction Not Found"))
                .when(transactionService).getTransaction(anyLong());

        // make a fetch transaction with non-existing 'id'
        mockMvc.perform(get("/api/transaction/7682"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Transaction Not Found"))
                .andExpect(jsonPath("$.StatusCode").value(404));
    }

    /* ******************** UPDATE Transaction Tests ******************** */
    @Test
    void updateTransaction_ValidId_ReturnSuccess_Message() throws Exception {

    }

}
