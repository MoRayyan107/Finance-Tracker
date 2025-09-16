package com.rayyan.finance_tracker.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayyan.finance_tracker.entity.Transaction;
import com.rayyan.finance_tracker.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@WebMvcTest(TransactionController.class) // Tests only the controller
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc; // Stimulates HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // converts object to JSON format

    @MockitoBean
    private TransactionService transactionService; // Mocks the service layer

    private static final LocalDateTime TEST_DATE = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    // converts the transaction object to JSON
    public String ValidTransactionCreation_To_JSON() throws JsonProcessingException {
        Transaction transaction = new Transaction();
        transaction.setDescription("Test Shopping Transaction");
        transaction.setAmount(new BigDecimal("3000.00"));
        transaction.setTransactionType(Transaction.TransactionType.EXPENSE);
        transaction.setCategory("Shopping");
        transaction.setDate(TEST_DATE);

        return objectMapper.writeValueAsString(transaction);
    }

    /* ******************** CREATE Transaction Tests ******************** */
    @Test
    void transactionCreation_ValidBody(){
        
    }

}
