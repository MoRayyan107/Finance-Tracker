package com.rayyan.finance_tracker.repository;

import com.rayyan.finance_tracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // finds all transactions based on its "Type"
    List<Transaction> findTransactionByTransactionType(Transaction.TransactionType transactionType);

    // gets all transactions by its "Category" -> Bills, Salary, Food etc.
    List<Transaction> findTransactionByCategory(String category);

    Transaction findByid(Long id);
}
