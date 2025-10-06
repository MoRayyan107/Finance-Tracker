package com.rayyan.finance_tracker.controllers;

import com.rayyan.finance_tracker.entity.Transaction;
import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.exceptions.TransactionNotFoundException;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.service.TransactionService;
import com.rayyan.finance_tracker.service.UserDetailService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserDetailService userDetailService;

    // Helper method to get current authenticated user
    /**
     * Get the current authenticated user from the security context
     * 
     * @return User object of the currently authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userDetailService.getUserByUsername(username);
    }

    /**
     * Making a new Transaction
     * 
     * @param transaction transaction object
     * @return success message if created
     * @Throws ValidationException if validation fails (EG negative amount, empty description)
     */
    @PostMapping("/create")
    public String createTransaction(@RequestBody Transaction transaction) {
        // Get current user from JWT token
        User currentUser = getCurrentUser();
        transaction.setUser(currentUser);

        transactionService.createTransaction(transaction);
        return "Transaction Successfully created";
    }

    /** 
     * Fetch all transactions for the current user
     * 
     * @return List of Transaction objects
     * @throws TransactionNotFoundException if no transactions found
     */
    @GetMapping("/fetchAll")
    public List<Transaction> getAllTransactions() {
        // Get only transactions for current user
        User currentUser = getCurrentUser();
        return transactionService.findTransactionsByUser(currentUser);
    }

    /**
     * Fetch a transaction by its ID for the current user
     * 
     * @param id transaction ID
     * @return Transaction object if found
     * @throws TransactionNotFoundException if transaction not found or user has no permission
     */
    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return transactionService.getTransactionByIdAndUser(id, currentUser);
    }

    /**
     * Update a transaction by its ID for the current user
     * 
     * @param id transaction ID
     * @param transaction updated transaction object
     * @return success message if updated
     * @throws TransactionNotFoundException if transaction not found or user has no permission
     * @throws ValidationException if validation fails (EG negative amount, empty description)
     */
    @PutMapping("/update/{id}")
    public String updateTransactionById(@PathVariable Long id, @RequestBody Transaction transaction) {
        User currentUser = getCurrentUser();
        return transactionService.updateTransaction(id, transaction, currentUser);
    }

    /**
     * Delete a transaction by its ID for the current user
     * 
     * @param id transaction ID
     * @return success message if deleted
     * @throws TransactionNotFoundException if transaction not found or user has no permission
     */
    @DeleteMapping("/delete/{id}")
    public String deleteTransactionById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return transactionService.deleteTransaction(id, currentUser);
    }
}