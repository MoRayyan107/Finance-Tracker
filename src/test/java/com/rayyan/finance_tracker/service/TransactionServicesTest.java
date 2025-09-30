//package com.rayyan.finance_tracker.service;
//
//import com.rayyan.finance_tracker.entity.Transaction;
//import com.rayyan.finance_tracker.exceptions.TransactionNotFoundException;
//import com.rayyan.finance_tracker.exceptions.ValidationException;
//import com.rayyan.finance_tracker.repository.TransactionRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)   // Enables Mockito
//public class TransactionServicesTest {
//
//    @Mock  // Fake repository
//    private TransactionRepository transactionRepository;
//
//    @InjectMocks   // real services with fake repo
//    private TransactionService transactionService;
//
//    private Transaction ValidTransaction;
//
//    private Transaction InvalidTransaction;
//
//    // to make easier for Time-based fields
//    private static final LocalDateTime TEST_DATE = LocalDateTime.of(2025, 6, 1, 7, 0, 0);
//
//    @BeforeEach
//    public void setup(){
//        // Valid Transaction
//        ValidTransaction = new Transaction();
//        ValidTransaction.setDescription("Test Clothes Shopping");
//        ValidTransaction.setAmount(new BigDecimal("4000.00"));
//        ValidTransaction.setCategory("Shopping");
//        ValidTransaction.setTransactionType(Transaction.TransactionType.EXPENSE);
//        ValidTransaction.setDate(TEST_DATE);
//
//        // invalid transaction
//        InvalidTransaction = new Transaction();
//        InvalidTransaction.setDescription("Failed Transaction");
//        InvalidTransaction.setAmount(new BigDecimal("5000.00"));
//        // <- MISSING: missing Category, Type and Date
//    }
//
//    /* ******************** Creating a Transaction ******************** */
//    @Test
//    void ValidTransaction_Success(){
//        // Given
//        when(transactionRepository.save(any(Transaction.class))).thenReturn(ValidTransaction);
//
//        // When
//        transactionService.createTransaction(ValidTransaction);
//
//        // verify
//        verify(transactionRepository, times(1)).save(any(Transaction.class));
//    }
//
//    @Test
//    void InvalidTransaction_Fail(){
//        assertThrows(ValidationException.class, () -> {
//            transactionService.createTransaction(InvalidTransaction);
//                }
//        );
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//
//    /* ******************** Creating a Transaction with Validation checks  ******************** */
//
//    @Test
//    void ValidateTransaction_Amount_Negatives(){
//        ValidTransaction.setAmount(new  BigDecimal("-5000.00"));
//        assertThrows(ValidationException.class, () -> {
//            transactionService.createTransaction(ValidTransaction);
//        });
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//
//    @Test
//    void ValidateTransaction_Description_Is_Null(){
//        ValidTransaction.setDescription(null);
//        assertThrows(ValidationException.class, () -> {
//            transactionService.createTransaction(ValidTransaction);
//        });
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//
//    @Test
//    void ValidateTransaction_Description_With_White_Spaces(){
//        ValidTransaction.setDescription("    ");
//        assertThrows(ValidationException.class, () -> {
//            transactionService.createTransaction(ValidTransaction);
//        });
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//
//    @Test
//    void ValidateTransaction_Category_Is_Null(){
//        ValidTransaction.setCategory(null);
//        assertThrows(ValidationException.class, () -> {
//            transactionService.createTransaction(ValidTransaction);
//        });
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//
//    @Test
//    void ValidateTransaction_Category_With_White_Spaces(){
//        ValidTransaction.setCategory("    ");
//        assertThrows(ValidationException.class, () -> {
//            transactionService.createTransaction(ValidTransaction);
//        });
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//
//    @Test
//    void ValidateTransaction_Date_Is_Null(){
//        ValidTransaction.setDate(null);
//        assertThrows(ValidationException.class, () -> {
//            transactionService.createTransaction(ValidTransaction);
//        });
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//
//    /* ******************** findian a List of Transactions ******************** */
//
//    @Test
//    void getAllTransaction_ExistingList(){
//        // Given
//        List<Transaction> expected = Arrays.asList(ValidTransaction);
//        when(transactionRepository.findAll()).thenReturn(expected);
//
//        // When
//        List<Transaction> res = transactionService.findAllTransactions();
//
//        // Assert and verify
//        assertEquals(1, res.size());
//        assertEquals("Shopping", res.get(0).getCategory());
//        verify(transactionRepository, times(1)).findAll();
//    }
//
//    @Test
//    void getAllTransaction_EmptyList(){
//        // Given
//        List<Transaction> expected = Arrays.asList();
//        when(transactionRepository.findAll()).thenReturn(expected);
//
//        // When
//        List<Transaction> res = transactionService.findAllTransactions();
//
//        // Assert and verify
//        assertEquals(0, res.size());
//        verify(transactionRepository, times(1)).findAll();
//    }
//
//    /* ******************** findian Transaction based on ID ******************** */
//
//    @Test
//    void getTransaction_ExistingId(){
//        // Given
//        when(transactionRepository.findById(1L)).thenReturn(Optional.of(ValidTransaction));
//
//        // When
//        Transaction res = transactionService.getTransaction(1L);
//
//        // assert and Verify
//        assertNotNull(res); // check if the object is NOT null
//        assertEquals("Shopping", res.getCategory());
//        verify(transactionRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    void getTransaction_Non_ExistingId(){
//        // Given
//        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // test and verify
//        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransaction(1L));
//        verify(transactionRepository, times(1)).findById(1L);
//    }
//
//    /* ******************** Update Transaction based on ID ******************** */
//    @Test
//    void updateTransaction_ExistingId_Valid_Transaction(){
//        // Given
//        when(transactionRepository.findById(1L)).thenReturn(Optional.of(ValidTransaction));
//
//        Transaction updated_Valid_Transaction = new Transaction();
//        updated_Valid_Transaction.setDescription("Bought XBox Series X");
//        updated_Valid_Transaction.setAmount(new BigDecimal("699.99"));
//        updated_Valid_Transaction.setTransactionType(Transaction.TransactionType.EXPENSE);
//        updated_Valid_Transaction.setCategory("Shopping");
//        updated_Valid_Transaction.setDate(TEST_DATE);
//
//        // When
//        String res = transactionService.updateTransaction(1L, updated_Valid_Transaction);
//
//        // assert Ans Verify
//        assertEquals("Transaction updated with ID: 1", res);
//        verify(transactionRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    void update_Transaction_ExistingID_Invalid_Transaction(){
//        // Given -> unnecessary sice validation will fail
//        Transaction updated_Invalid_Transaction = new Transaction();
//        updated_Invalid_Transaction.setDescription("Bought XBox Series X");
//        updated_Invalid_Transaction.setAmount(new BigDecimal("699.99"));
//        updated_Invalid_Transaction.setTransactionType(null); // will throw ValidationException
//
//        // assert and verify
//        assertThrows(ValidationException.class,
//                () -> transactionService.updateTransaction(1L, updated_Invalid_Transaction));
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(transactionRepository, never()).findById(1L);
//    }
//
//    @Test
//    void Update_Transaction_Non_ExistingID_Valid_Transaction(){
//        // Given
//        when(transactionRepository.findById(5L)).thenReturn(Optional.empty());
//
//        // assert and verify
//        assertThrows(TransactionNotFoundException.class,
//                () -> transactionService.updateTransaction(5L, ValidTransaction));
//        verify(transactionRepository, times(1)).findById(5L);
//    }
//
//    /* ******************** Deleting a Transaction based on ID ******************** */
//
//    @Test
//    void Delete_Transaction_ExistingId(){
//        // Given
//        when(transactionRepository.findById(1L)).thenReturn(Optional.of(ValidTransaction));
//
//        // When
//        String res = transactionService.deleteTransaction(1L);
//
//        // assert and verify
//        assertEquals("Transaction deleted with ID: 1", res);
//        verify(transactionRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    void Delete_Transaction_Non_ExistingId(){
//        // Given
//        when(transactionRepository.findById(4L)).thenReturn(Optional.empty());
//
//        // assert and verify
//        assertThrows(TransactionNotFoundException.class,
//                () -> transactionService.deleteTransaction(4L));
//        verify(transactionRepository, times(1)).findById(4L);
//    }
//
//}
