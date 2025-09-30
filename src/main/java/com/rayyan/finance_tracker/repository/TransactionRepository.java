package com.rayyan.finance_tracker.repository;

import com.rayyan.finance_tracker.entity.Transaction;
import com.rayyan.finance_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find all transactions for a specific user
    List<Transaction> findByUser(User user);

    // Find transaction by ID and User (for security - users can only access their own transactions)
    Optional<Transaction> findByIdAndUser(Long id, User user);

    // Find transactions by type for a specific user
    List<Transaction> findByTransactionTypeAndUser(Transaction.TransactionType transactionType, User user);

    // Find transactions by category for a specific user
    List<Transaction> findByCategoryAndUser(String category, User user);
}