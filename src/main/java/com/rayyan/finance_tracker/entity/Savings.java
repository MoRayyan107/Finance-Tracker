package com.rayyan.finance_tracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rayyan.finance_tracker.exceptions.InsufficientFundsException;
import com.rayyan.finance_tracker.exceptions.InvalidAmountException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "savings")
public class Savings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String savingsName;

    @Column(nullable = false, length = 500)
    private String savingsDescription;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal currentAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @Column(nullable = false, columnDefinition = "datetime")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "datetime") // updates after a user modifies the goal
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingsStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    /**
     * Status codes for goals:
     * IN_PROGRESS -> the goal has not yet reached its target value
     * COMPLETED   -> the goal has reached its target value
     */
    public enum SavingsStatus {
        IN_PROGRESS,
        COMPLETED
    }

    /**
     * Gets the completion percentage of a goal.
     *
     * @return String stating, In Progress 20%, Completed 100%, Exceeded By 24%
     */
    public String getCompletion() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return "In Progress (0%)";
        }

        var completion = currentAmount
                .divide(targetAmount, 2, RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_EVEN);

        String status;
        BigDecimal hundred =  BigDecimal.valueOf(100);

        if (completion.compareTo(BigDecimal.valueOf(100)) < 0)
            status = "In Progress ";
        else if (completion.compareTo(BigDecimal.valueOf(100)) == 0)
            status = "Completed ";
        else {
            status = "Exceeded By ";
            completion = completion.subtract(hundred);
        }

        return status + completion + "%";
    }

    /**
     * Adds funds to the goal.
     *
     * @param amountToAdd amount to add
     * @throws InvalidAmountException if the amount is <= 0
     */
    public void addToSavings(BigDecimal amountToAdd) {
        if (amountToAdd == null || amountToAdd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
        this.currentAmount = currentAmount.add(amountToAdd);
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    /**
     * Withdraws funds from the goal.
     *
     * @param amountToWithdraw amount to withdraw
     * @throws InvalidAmountException     if amount <= 0
     * @throws InsufficientFundsException if withdrawal > current balance
     */
    public void withdrawFromSaving(BigDecimal amountToWithdraw) {
        if (amountToWithdraw == null || amountToWithdraw.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
        if (this.currentAmount.compareTo(amountToWithdraw) < 0) {
            throw new InsufficientFundsException("Withdrawal amount exceeds current savings amount");
        }

        this.currentAmount = currentAmount.subtract(amountToWithdraw);
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    /**
     * Updates the goal status based on progress.
     */
    private void updateStatus() {
        if (currentAmount.compareTo(targetAmount) >= 0) {
            this.status = SavingsStatus.COMPLETED;
        } else {
            this.status = SavingsStatus.IN_PROGRESS;
        }
    }

    // JPA lifecycle callbacks
    /*
     * @PrePresets add's data before saving into the database
     * this annotation runs before inserting into new entity
     *
     * Adds currentAmount, Status to its default values instead of manual setting itr
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (currentAmount == null) {
            currentAmount = BigDecimal.ZERO;
        }
        if (status == null) {
            status = SavingsStatus.IN_PROGRESS;
        }
        // if current Amount is existing on creation
        updateStatus();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateStatus();
    }
}
