package com.rayyan.finance_tracker.controllers;

import com.rayyan.finance_tracker.entity.Transaction;
import com.rayyan.finance_tracker.entity.User;
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
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userDetailService.getUserByUsername(username);
    }

    @PostMapping("/create")
    public String createTransaction(@RequestBody Transaction transaction) {
        // Get current user from JWT token
        User currentUser = getCurrentUser();
        transaction.setUser(currentUser);

        transactionService.createTransaction(transaction);
        return "Transaction Successfully created";
    }

    @GetMapping("/fetchAll")
    public List<Transaction> getAllTransactions() {
        // Get only transactions for current user
        User currentUser = getCurrentUser();
        return transactionService.findTransactionsByUser(currentUser);
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return transactionService.getTransactionByIdAndUser(id, currentUser);
    }

    @PutMapping("/update/{id}")
    public String updateTransactionById(@PathVariable Long id, @RequestBody Transaction transaction) {
        User currentUser = getCurrentUser();
        return transactionService.updateTransaction(id, transaction, currentUser);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteTransactionById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return transactionService.deleteTransaction(id, currentUser);
    }
}