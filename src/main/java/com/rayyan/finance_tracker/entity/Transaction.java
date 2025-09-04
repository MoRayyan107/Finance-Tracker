package com.rayyan.finance_tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity             // tells JPA, "This class represents a table."
@Data               // Lombok annotation -> gets all getters, setters and toString methods
@NoArgsConstructor  // makes constructor with nor arguments
@AllArgsConstructor // makes a constructor with arguments
@Table(name = "transactions") // table name
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto generates a unique id for each entry
    private Long Id;

    @Column(nullable = false)
    private String description;

    // amount of transaction
    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // converts the Enum to String
    private TransactionType transactionType;

    @Column(nullable = false)
    private String category;  // Bills, Salary, Food etc.

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime date;

    // What type of transaction
    // this can be done in Enum class
    public enum TransactionType{
        INCOME, EXPENSE
    }
}
