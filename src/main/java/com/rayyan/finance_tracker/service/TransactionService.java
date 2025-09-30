package com.rayyan.finance_tracker.service;

import com.rayyan.finance_tracker.entity.Transaction;
import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.exceptions.TransactionNotFoundException;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createTransaction(Transaction transaction) {
        validateTransaction(transaction);
        transactionRepository.save(transaction);
        logger.info("Transaction created for user: {}", transaction.getUser().getUsername());
    }

    public List<Transaction> findTransactionsByUser(User user) {
        logger.info("Getting all transactions for user: {}", user.getUsername());
        return transactionRepository.findByUser(user);
    }

    public Transaction getTransactionByIdAndUser(Long id, User user) {
        return transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found or you don't have permission to access it"));
    }

    public String updateTransaction(Long id, Transaction transaction, User user) {
        validateTransaction(transaction);
        Transaction existingTransaction = getTransactionByIdAndUser(id, user);

        existingTransaction.setDescription(transaction.getDescription());
        existingTransaction.setAmount(transaction.getAmount());
        existingTransaction.setCategory(transaction.getCategory());
        existingTransaction.setDate(transaction.getDate());
        existingTransaction.setTransactionType(transaction.getTransactionType());

        transactionRepository.save(existingTransaction);
        logger.info("Transaction updated for user: {}", user.getUsername());
        return "Transaction updated with ID: " + id;
    }

    public String deleteTransaction(Long id, User user) {
        Transaction transaction = getTransactionByIdAndUser(id, user);
        transactionRepository.delete(transaction);
        logger.info("Transaction deleted for user: {}", user.getUsername());
        return "Transaction deleted with ID: " + id;
    }

    private void validateTransaction(Transaction transaction) {
        logger.info("Validating transaction......");

        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Amount must be greater than zero");

        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty())
            throw new ValidationException("Description cannot be empty");

        if (transaction.getTransactionType() == null)
            throw new ValidationException("Transaction Type cannot be empty");

        if (transaction.getCategory() == null || transaction.getCategory().trim().isEmpty())
            throw new ValidationException("Category cannot be empty");

        if (transaction.getDate() == null)
            throw new ValidationException("Date is a Required Field");

        if (transaction.getUser() == null)
            throw new ValidationException("User is required");
    }
}