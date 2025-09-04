package com.rayyan.finance_tracker.service;

import com.rayyan.finance_tracker.entity.Transaction;
import com.rayyan.finance_tracker.exceptions.TransactionNotFoundException;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // creates a new Transaction
    public void createTransaction(Transaction transaction){
        validateTransaction(transaction); // validates if any missing columns exist if so throws an exception
        transactionRepository.save(transaction);
    }

    // returns all transactions
    public List<Transaction> findAllTransactions(){
        return transactionRepository.findAll();
    }

    // get a transaction object by Transaction ID
    public Transaction getTransaction(Long id){
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
    }

    // updates an existing transaction by its id
    // will throw exception if not found
    public String updateTransaction(Long id, Transaction transaction){
        validateTransaction(transaction); // validates if any missing columns exist if so throws an exception
        Transaction updatedTransaction = getTransaction(id);

        updatedTransaction.setDescription(transaction.getDescription());
        updatedTransaction.setAmount(transaction.getAmount());
        updatedTransaction.setCategory(transaction.getCategory());
        updatedTransaction.setDate(transaction.getDate());
        updatedTransaction.setTransactionType(transaction.getTransactionType());

        transactionRepository.save(updatedTransaction);
        return "Transaction updated with ID: "+id;

    }

    // deletes an existing transaction by id
    // will throw exception if not found
    public String deleteTransaction(Long id){
        Transaction transaction = getTransaction(id);
        transactionRepository.delete(transaction);

        return "Transaction deleted with ID: " + id;
    }

    // validates if the transaction is legit (if the entries match to the entity description)
    private void validateTransaction(Transaction transaction){
        // transaction cant be less than 0
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Amount must be greater than zero");

        // description cannot be null or have white empty spaces with no characters
        if (transaction.getDescription() == null || transaction.getDescription().isEmpty())
            throw new ValidationException("Description cannot be empty");

        // if our transaction type (enum) is null
        if (transaction.getTransactionType() == null)
            throw new ValidationException("Transaction Type cannot be empty");

        // the category cannot be null or have any white space with no characters
        if (transaction.getCategory() == null || transaction.getCategory().trim().isEmpty())
            throw new ValidationException("Category cannot be empty");

        if (transaction.getDate() == null)
            throw new ValidationException("Date is a Required Field");
    }
}
