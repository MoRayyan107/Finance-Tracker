package com.rayyan.finance_tracker.service;

import com.rayyan.finance_tracker.entity.Transaction;
import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.exceptions.TransactionNotFoundException;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.repository.TransactionRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServicesTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction ValidTransaction;
    private Transaction InvalidTransaction;
    private User currentUser;

    private static final Map<Integer, String> test_Passes = new HashMap<>();
    private static final LocalDateTime TEST_DATE = LocalDateTime.of(2025, 6, 1, 7, 0, 0);

    @BeforeAll
    static void beforeAll() {
        System.out.println("\n--- Starting TransactionService Tests ---");
    }

    @BeforeEach
    public void setup() {
        currentUser = User.builder()
                .id(1L)
                .role(User.Role.USER)
                .username("TestUser123")
                .build();

        ValidTransaction = new Transaction();
        ValidTransaction.setDescription("Test Clothes Shopping");
        ValidTransaction.setAmount(new BigDecimal("4000.00"));
        ValidTransaction.setCategory("Shopping");
        ValidTransaction.setTransactionType(Transaction.TransactionType.EXPENSE);
        ValidTransaction.setDate(TEST_DATE);
        ValidTransaction.setUser(currentUser);

        InvalidTransaction = new Transaction();
        InvalidTransaction.setDescription("Failed Transaction");
        InvalidTransaction.setAmount(new BigDecimal("5000.00"));
        InvalidTransaction.setUser(currentUser);
    }

    /* ******************** Creating a Transaction ******************** */
    @Test
    void ValidTransaction_Success() {
        transactionService.createTransaction(ValidTransaction);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        test_Passes.put(1, "Create: Valid Transaction");
    }

    @Test
    void InvalidTransaction_Fail() {
        assertThrows(ValidationException.class, () -> {
            transactionService.createTransaction(InvalidTransaction);
        });
        verify(transactionRepository, never()).save(any(Transaction.class));
        test_Passes.put(2, "Create: Invalid Transaction Throws Exception");
    }

    /*
     * ******************** Creating a Transaction with Validation checks
     * ********************
     */

    @Test
    void ValidateTransaction_Amount_Negatives() {
        ValidTransaction.setAmount(new BigDecimal("-5000.00"));
        assertThrows(ValidationException.class, () -> {
            transactionService.createTransaction(ValidTransaction);
        });
        verify(transactionRepository, never()).save(any(Transaction.class));
        test_Passes.put(3, "Validate: Amount Negative Throws Exception");
    }

    @Test
    void ValidateTransaction_Description_Is_Null() {
        ValidTransaction.setDescription(null);
        assertThrows(ValidationException.class, () -> {
            transactionService.createTransaction(ValidTransaction);
        });
        verify(transactionRepository, never()).save(any(Transaction.class));
        test_Passes.put(4, "Validate: Description Null Throws Exception");
    }

    @Test
    void ValidateTransaction_Description_With_White_Spaces() {
        ValidTransaction.setDescription("    ");
        assertThrows(ValidationException.class, () -> {
            transactionService.createTransaction(ValidTransaction);
        });
        verify(transactionRepository, never()).save(any(Transaction.class));
        test_Passes.put(5, "Validate: Description Whitespace Throws Exception");
    }

    @Test
    void ValidateTransaction_Category_Is_Null() {
        ValidTransaction.setCategory(null);
        assertThrows(ValidationException.class, () -> {
            transactionService.createTransaction(ValidTransaction);
        });
        verify(transactionRepository, never()).save(any(Transaction.class));
        test_Passes.put(6, "Validate: Category Null Throws Exception");
    }

    @Test
    void ValidateTransaction_Category_With_White_Spaces() {
        ValidTransaction.setCategory("    ");
        assertThrows(ValidationException.class, () -> {
            transactionService.createTransaction(ValidTransaction);
        });
        verify(transactionRepository, never()).save(any(Transaction.class));
        test_Passes.put(7, "Validate: Category Whitespace Throws Exception");
    }

    @Test
    void ValidateTransaction_Date_Is_Null() {
        ValidTransaction.setDate(null);
        assertThrows(ValidationException.class, () -> {
            transactionService.createTransaction(ValidTransaction);
        });
        verify(transactionRepository, never()).save(any(Transaction.class));
        test_Passes.put(8, "Validate: Date Null Throws Exception");
    }

    /* ******************** Finding a List of Transactions ******************** */

    @Test
    void getAllTransaction_ExistingList() {
        List<Transaction> expected = List.of(ValidTransaction);
        when(transactionRepository.findByUser(currentUser)).thenReturn(expected);

        List<Transaction> res = transactionService.findTransactionsByUser(currentUser);

        assertEquals(1, res.size());
        assertEquals("Shopping", res.get(0).getCategory());
        assertEquals("TestUser123", res.get(0).getUser().getUsername());
        verify(transactionRepository, times(1)).findByUser(currentUser);
        test_Passes.put(9, "Find: Get All Transactions With Existing List");
    }

    @Test
    void getAllTransaction_EmptyList() {
        List<Transaction> expected = List.of();
        when(transactionRepository.findByUser(currentUser)).thenReturn(expected);

        List<Transaction> res = transactionService.findTransactionsByUser(currentUser);

        assertEquals(0, res.size());
        verify(transactionRepository, times(1)).findByUser(currentUser);
        test_Passes.put(10, "Find: Get All Transactions With Empty List");
    }

    /*
     * ******************** Finding Transaction based on ID and USER
     * ********************
     */

    @Test
    void getTransaction_ExistingId() {
        when(transactionRepository.findByIdAndUser(1L, currentUser))
                .thenReturn(Optional.of(ValidTransaction));

        Transaction res = transactionService.getTransactionByIdAndUser(1L, currentUser);

        assertNotNull(res);
        assertEquals("Shopping", res.getCategory());
        assertEquals("TestUser123", res.getUser().getUsername());
        verify(transactionRepository, times(1)).findByIdAndUser(1L, currentUser);
        test_Passes.put(11, "Find: Get Transaction By Existing ID");
    }

    @Test
    void getTransaction_Non_ExistingId() {
        when(transactionRepository.findByIdAndUser(1L, currentUser))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService
                .getTransactionByIdAndUser(1L, currentUser));
        verify(transactionRepository, times(1)).findByIdAndUser(1L, currentUser);
        test_Passes.put(12, "Find: Get Transaction By Non-Existing ID Throws Exception");
    }

    /* ******************** Update Transaction based on ID ******************** */
    @Test
    void updateTransaction_ExistingId_Valid_Transaction() {
        when(transactionRepository.findByIdAndUser(1L, currentUser))
                .thenReturn(Optional.of(ValidTransaction));

        Transaction updated_Valid_Transaction = new Transaction();
        updated_Valid_Transaction.setDescription("Bought XBox Series X");
        updated_Valid_Transaction.setAmount(new BigDecimal("699.99"));
        updated_Valid_Transaction.setTransactionType(Transaction.TransactionType.EXPENSE);
        updated_Valid_Transaction.setCategory("Shopping");
        updated_Valid_Transaction.setDate(TEST_DATE);
        updated_Valid_Transaction.setUser(currentUser);

        String res = transactionService.updateTransaction(1L, updated_Valid_Transaction, currentUser);

        assertEquals("Transaction updated with ID: 1", res);
        verify(transactionRepository, times(1)).findByIdAndUser(1L, currentUser);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        test_Passes.put(13, "Update: Existing ID With Valid Transaction");
    }

    @Test
    void update_Transaction_ExistingID_Invalid_Transaction() {
        Transaction updated_Invalid_Transaction = new Transaction();
        updated_Invalid_Transaction.setDescription("Bought XBox Series X");
        updated_Invalid_Transaction.setAmount(new BigDecimal("699.99"));
        updated_Invalid_Transaction.setTransactionType(null);

        assertThrows(ValidationException.class, () -> transactionService
                .updateTransaction(1L, updated_Invalid_Transaction, currentUser));

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transactionRepository, never()).findByIdAndUser(1L, currentUser);
        test_Passes.put(14, "Update: Existing ID With Invalid Transaction Throws Exception");
    }

    @Test
    void Update_Transaction_Non_ExistingID_Valid_Transaction() {
        when(transactionRepository.findByIdAndUser(5L, currentUser))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService
                .updateTransaction(5L, ValidTransaction, currentUser));

        verify(transactionRepository, times(1)).findByIdAndUser(5L, currentUser);
        verify(transactionRepository, never()).save(any(Transaction.class));
        test_Passes.put(15, "Update: Non-Existing ID With Valid Transaction Throws Exception");
    }

    /*
     * ******************** Deleting a Transaction based on ID ********************
     */

    @Test
    void Delete_Transaction_ExistingId() {
        when(transactionRepository.findByIdAndUser(1L, currentUser)).thenReturn(Optional.of(ValidTransaction));

        String res = transactionService.deleteTransaction(1L, currentUser);

        assertEquals("Transaction deleted with ID: 1", res);
        verify(transactionRepository, times(1)).findByIdAndUser(1L, currentUser);
        verify(transactionRepository, times(1)).delete(any(Transaction.class));
        test_Passes.put(16, "Delete: Transaction With Existing ID");
    }

    @Test
    void Delete_Transaction_Non_ExistingId() {
        when(transactionRepository.findByIdAndUser(4L, currentUser))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> transactionService.deleteTransaction(4L, currentUser));

        verify(transactionRepository, times(1)).findByIdAndUser(4L, currentUser);
        verify(transactionRepository, never()).delete(any(Transaction.class));
        test_Passes.put(17, "Delete: Transaction With Non-Existing ID Throws Exception");
    }

    @AfterAll
    static void afterAll() {
        int maxLength = 0;
        int totalTests = 17;
        int passedTests = test_Passes.size();

        // Separate tests by operation type
        Map<Integer, String> createTests = new TreeMap<>();
        Map<Integer, String> validateTests = new TreeMap<>();
        Map<Integer, String> findTests = new TreeMap<>();
        Map<Integer, String> updateTests = new TreeMap<>();
        Map<Integer, String> deleteTests = new TreeMap<>();

        for (Map.Entry<Integer, String> entry : test_Passes.entrySet()) {
            String testName = entry.getValue();
            if (testName.startsWith("Create:")) {
                createTests.put(entry.getKey(), testName);
            } else if (testName.startsWith("Validate:")) {
                validateTests.put(entry.getKey(), testName);
            } else if (testName.startsWith("Find:")) {
                findTests.put(entry.getKey(), testName);
            } else if (testName.startsWith("Update:")) {
                updateTests.put(entry.getKey(), testName);
            } else if (testName.startsWith("Delete:")) {
                deleteTests.put(entry.getKey(), testName);
            }

            if (testName.length() > maxLength) {
                maxLength = testName.length();
            }
        }

        String format = "Test %-2d: %-" + maxLength + "s -> %s%n";

        System.out.println("\n--- TransactionService Test Results ---");

        if (!createTests.isEmpty()) {
            System.out.println("\n=== CREATE TESTS ===");
            for (Map.Entry<Integer, String> entry : createTests.entrySet()) {
                System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
            }
        }

        if (!validateTests.isEmpty()) {
            System.out.println("\n=== VALIDATION TESTS ===");
            for (Map.Entry<Integer, String> entry : validateTests.entrySet()) {
                System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
            }
        }

        if (!findTests.isEmpty()) {
            System.out.println("\n=== FIND TESTS ===");
            for (Map.Entry<Integer, String> entry : findTests.entrySet()) {
                System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
            }
        }

        if (!updateTests.isEmpty()) {
            System.out.println("\n=== UPDATE TESTS ===");
            for (Map.Entry<Integer, String> entry : updateTests.entrySet()) {
                System.out.printf(format, entry.getKey(), entry.getValue(), "PASS");
            }
        }

        if (!deleteTests.isEmpty()) {
            System.out.println("\n=== DELETE TESTS ===");
            for (Map.Entry<Integer, String> entry : deleteTests.entrySet()) {
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