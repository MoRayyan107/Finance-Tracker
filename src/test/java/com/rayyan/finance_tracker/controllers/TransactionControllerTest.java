package com.rayyan.finance_tracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayyan.finance_tracker.entity.Transaction;
import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.exceptions.TransactionNotFoundException;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.service.TransactionService;
import com.rayyan.finance_tracker.service.UserDetailService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.rayyan.finance_tracker.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = VALID_USERNAME, password = VALID_PASSWORD) // Simulates a logged-in user
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserDetailService userDetailService;


    private static final Map<Integer, String> test_Passes = new HashMap<>();

    // transaction for salary
    private Transaction validTransactionSalary;
    // transaction for expenses
    private Transaction validTransactionExpenses;
    private Transaction invalidTransaction;
    private Transaction updatedTransaction;
    private User testUser;

    @BeforeEach
    public void setup() {
        User testUser = User.builder()
                .id(1L)
                .password(VALID_PASSWORD)
                .build();

        // return the user object when trying to find one
        when(userDetailService.getUserByUsername(VALID_USERNAME)).thenReturn(testUser);

        // Valid Transaction for Salary
        validTransactionSalary = new Transaction();
        validTransactionSalary.setUser(testUser);
        validTransactionSalary.setId(1L);
        validTransactionSalary.setAmount(new BigDecimal(200));
        validTransactionSalary.setTransactionType(Transaction.TransactionType.INCOME);
        validTransactionSalary.setDescription("test transaction salary");
        validTransactionSalary.setCategory("Salary");
        validTransactionSalary.setDate(TEST_DATE);

        // Transaction for Expenses
        validTransactionExpenses = new Transaction();
        validTransactionExpenses.setUser(testUser);
        validTransactionExpenses.setId(1L);
        validTransactionExpenses.setAmount(new BigDecimal(600));
        validTransactionExpenses.setTransactionType(Transaction.TransactionType.EXPENSE);
        validTransactionExpenses.setDescription("test transaction Expenses");
        validTransactionExpenses.setCategory("Expenses");
        validTransactionExpenses.setDate(TEST_DATE);

        // Invalid Transaction
        invalidTransaction = new Transaction();
        invalidTransaction.setUser(null); // -> throws validation error
        invalidTransaction.setId(1L);
        invalidTransaction.setAmount(new BigDecimal(-678));
        invalidTransaction.setTransactionType(Transaction.TransactionType.INCOME);
        invalidTransaction.setDescription("test transaction income");
        invalidTransaction.setCategory("Income");
        invalidTransaction.setDate(TEST_DATE);

        // updating a valid transaction
        updatedTransaction = new Transaction();
        updatedTransaction.setDescription("Updated Description");
        updatedTransaction.setAmount(new BigDecimal("150.00"));
        updatedTransaction.setTransactionType(Transaction.TransactionType.EXPENSE);
        updatedTransaction.setCategory("Utilities");
        updatedTransaction.setDate(LocalDateTime.now());
    }

    @Test
    void create_ValidTransaction_ReturnsSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(transactionService).createTransaction(any(Transaction.class));

        // Act
        mockMvc.perform(post(CREATE_TRANSACTION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTransactionSalary)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction Successfully created"));

        // Verify
        verify(transactionService, times(1)).createTransaction(any(Transaction.class));

        // pass the test
        test_Passes.put(1, "POST: create a Valid Transaction Returns Success Message ");
    }

    @Test
    void create_InvalidTransaction_ReturnsBadRequest() throws Exception {
        // Arrange
        doThrow(new ValidationException("Invalid transaction")).when(transactionService).createTransaction(any(Transaction.class));

        // Act
        mockMvc.perform(post(CREATE_TRANSACTION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTransaction)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failure"))
                .andExpect(jsonPath("$.message").value("Invalid transaction"));

        // Verify
        verify(transactionService, times(1)).createTransaction(any(Transaction.class));

        // pass the test
        test_Passes.put(2, "POST: create a In-Valid Transaction Returns Bad Request Message ");
    }

    @Test
    void fetchAllTransactions_ReturnsListOfTransactions() throws Exception {
        // Arrange
        List<Transaction> transactions = List.of(validTransactionSalary, validTransactionExpenses);
        when(transactionService.findTransactionsByUser(any(User.class))).thenReturn(transactions);

        // Act
        mockMvc.perform(get(FETCH_ALL_TRANSACTIONS_API))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(transactions.size()))
                .andExpect(jsonPath("$[0].description").value("test transaction salary"))
                .andExpect(jsonPath("$[1].description").value("test transaction Expenses"));

        // Verify
        verify(transactionService, times(1)).findTransactionsByUser(any(User.class));

        // pass the test
        test_Passes.put(3, "GET: Fetch all transactions for a user Returns a list of transactions");
    }

    @Test
    void fetchAllTransactions_NoTransactionsFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(transactionService.findTransactionsByUser(any(User.class))).thenThrow(new TransactionNotFoundException("No transactions found"));

        // Act
        mockMvc.perform(get(FETCH_ALL_TRANSACTIONS_API))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Transaction not found"))
                .andExpect(jsonPath("$.message").value("No transactions found"));

        // Verify
        verify(transactionService, times(1)).findTransactionsByUser(any(User.class));

        // pass the test
        test_Passes.put(4, "GET: Fetch all transactions for a user Returns No Transactions found");
    }

    @Test
    void getTransactionById_ReturnsTransaction() throws Exception {
        // Arrange
        when(transactionService.getTransactionByIdAndUser(eq(1L), any(User.class))).thenReturn(validTransactionSalary);

        // Act
        mockMvc.perform(get(FETCH_TRANSACTION_BY_ID_API, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("test transaction salary"));

        // Verify
        verify(transactionService, times(1)).getTransactionByIdAndUser(eq(1L), any(User.class));

        // pass the test
        test_Passes.put(5, "GET: fetch a Transaction by its ID Returns a Transaction");
    }

    @Test
    void getTransactionById_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(transactionService.getTransactionByIdAndUser(eq(1L), any(User.class))).thenThrow(new TransactionNotFoundException("Transaction not found"));

        // Act
        mockMvc.perform(get(FETCH_TRANSACTION_BY_ID_API, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Transaction not found"))
                .andExpect(jsonPath("$.message").value("Transaction not found"));

        // Verify
        verify(transactionService, times(1)).getTransactionByIdAndUser(eq(1L), any(User.class));

        // pass the test
        test_Passes.put(6, "GET: fetch a Transaction by its ID Returns Transaction not found");
    }

    @Test
    void updateTransactionById_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        when(transactionService.updateTransaction(eq(1L), any(Transaction.class), any(User.class))).thenReturn("Transaction Successfully updated");

        // Act
        mockMvc.perform(put(UPDATE_TRANSACTION_API, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTransaction)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction Successfully updated"));

        // Verify
        verify(transactionService, times(1)).updateTransaction(eq(1L), any(Transaction.class), any(User.class));

        // pass the test
        test_Passes.put(7, "PUT: Updates a transaction by its ID Returns Success Message ");
    }

    @Test
    void updateTransactionById_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(transactionService.updateTransaction(eq(1L), any(Transaction.class), any(User.class)))
                .thenThrow(new TransactionNotFoundException("Transaction not found"));

        // Act
        mockMvc.perform(put(UPDATE_TRANSACTION_API, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTransaction)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Transaction not found"))
                .andExpect(jsonPath("$.message").value("Transaction not found"));

        // Verify
        verify(transactionService, times(1)).updateTransaction(eq(1L), any(Transaction.class), any(User.class));

        // pass the test
        test_Passes.put(8, "PUT: Update a Transaction by its ID Returns Transaction Not Found Message ");
    }

    @Test
    void updateTransactionById_ValidationError_ReturnsBadRequest() throws Exception {
        // Arrange
        when(transactionService.updateTransaction(eq(1L), any(Transaction.class), any(User.class)))
                .thenThrow(new ValidationException("Validation failed"));

        // Act
        mockMvc.perform(put(UPDATE_TRANSACTION_API, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTransaction)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failure"))
                .andExpect(jsonPath("$.message").value("Validation failed"));

        // Verify
        verify(transactionService, times(1)).updateTransaction(eq(1L), any(Transaction.class), any(User.class));

        // pass the test
        test_Passes.put(9, "PUT: Updates a Transaction by its ID with invalid updated transaction Returns Bad Request");
    }

    @Test
    void deleteTransactionById_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        when(transactionService.deleteTransaction(eq(1L), any(User.class))).thenReturn("Transaction Successfully deleted");

        // Act
        mockMvc.perform(delete(DELETE_TRANSACTION_API,1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction Successfully deleted"));

        // Verify
        verify(transactionService, times(1)).deleteTransaction(eq(1L), any(User.class));

        // pass the test
        test_Passes.put(10, "DELETE: Deletes a transaction by its ID Returns Success Message ");
    }

    @Test
    void deleteTransactionById_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(transactionService.deleteTransaction(eq(1L), any(User.class)))
                .thenThrow(new TransactionNotFoundException("Transaction not found"));

        // Act
        mockMvc.perform(delete(DELETE_TRANSACTION_API,1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Transaction not found"))
                .andExpect(jsonPath("$.message").value("Transaction not found"));

        // Verify
        verify(transactionService, times(1)).deleteTransaction(eq(1L), any(User.class));
        // pass the test
        test_Passes.put(11, "DELETE: Deletes a transaction by its ID Returns Transaction Not Found Message ");
    }

    @AfterAll
    static void afterAll() {
        int maxLength = 0;
        int totalTests = 14;
        int passedTests = test_Passes.size();

        Map<Integer, String> PostMap = new TreeMap<>();
        Map<Integer, String> PutMap = new TreeMap<>();
        Map<Integer, String> GetMap = new TreeMap<>();
        Map<Integer, String> DelMap = new TreeMap<>();


        for (Map.Entry<Integer, String> entry : test_Passes.entrySet()) {
            String testName = entry.getValue();
            if (testName.startsWith("POST:")) {
                PostMap.put(entry.getKey(), testName);
            } else if (testName.startsWith("PUT:")) {
                PutMap.put(entry.getKey(), testName);
            } else if (testName.startsWith("GET:")) {
                GetMap.put(entry.getKey(), testName);
            } else if (testName.startsWith("DELETE:")) {
                DelMap.put(entry.getKey(), testName);
            }

            if (testName.length() > maxLength) {
                maxLength = testName.length();
            }
        }

        String format = "Test %-2d: %-" + maxLength + "s -> %s%n";

        System.out.println("\n--- TransactionController Test Results ---");

        System.out.println("\n=== POST TESTS ===");
        for (Map.Entry<Integer, String> entry : PostMap.entrySet()) {
            System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
        }

        System.out.println("\n=== PUT TESTS ===");
        for (Map.Entry<Integer, String> entry : PutMap.entrySet()) {
            System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
        }

        System.out.println("\n=== GET TESTS ===");
        for (Map.Entry<Integer, String> entry : GetMap.entrySet()) {
            System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
        }

        System.out.println("\n=== DELETE TESTS ===");
        for (Map.Entry<Integer, String> entry : DelMap.entrySet()) {
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


