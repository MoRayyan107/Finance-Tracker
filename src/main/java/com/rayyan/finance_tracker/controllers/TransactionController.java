package com.rayyan.finance_tracker.controllers;

import com.rayyan.finance_tracker.entity.Transaction;
import com.rayyan.finance_tracker.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // creates a new Transaction
    @PostMapping("/create")
    public String createTransaction(@RequestBody Transaction transaction) {
        transactionService.createTransaction(transaction);
        return "Transaction Successfully created";
    }

    // gets all the transaction
    @GetMapping("/fetch")
    public List<Transaction> getAllTransactions() {
        return transactionService.findAllTransactions();
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) {
        return transactionService.getTransaction(id);
    }

    @PutMapping("/update/{id}")
    public String updateTransactionById(@PathVariable Long id, @RequestBody Transaction transaction) {
        return transactionService.updateTransaction(id, transaction);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteTransactionById(@PathVariable Long id) {
        return transactionService.deleteTransaction(id);
    }


}
