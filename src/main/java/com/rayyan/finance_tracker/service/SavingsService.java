package com.rayyan.finance_tracker.service;

import com.rayyan.finance_tracker.entity.Savings;
import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.exceptions.SavingsException;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.repository.SavingsRepository;
import com.rayyan.finance_tracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsService {

    private static final Logger log = LoggerFactory.getLogger(SavingsService.class);

    private final SavingsRepository savingsRepository;

    /**
     * Finds all savings for a user
     * @param user The user for savings retrieval
     * @return a Success Message if savings was created
     */
    public List<Savings> findAllSavings(User user) {
        log.info("Finding all savings for user: {}", user.getUsername());
        return savingsRepository.findByUser(user);
    }

    /**
     * finds a savings by ID and User
     * @param id the savings id to search
     * @param user the user who owns the savings
     * @return Savings for the user
     * @throws SavingsException if the Savings not found for user
     */
    public Savings findSavingsByIdAndUser(Long id, User user) {
        log.info("Finding savings for user: {}", user.getUsername());
        return savingsRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new SavingsException("Savings not found for user: " + user.getUsername()));
    }

    /**
     * Creates a goal for a user
     * @param savings the goal object to save
     * @return a String to give a Success message
     */
    @Transactional
    public String createSavings(Savings savings) {
        log.info("Creating a new savings {}", savings);
        validateSavings(savings);
        savingsRepository.save(savings);
        log.info("Savings created with Idr: {}", savings.getId());
        return "Saving Goal added Successfully!";
    }

    /**
     * Updates the existing saving goal
     *
     * @param Id the goal id to get updated
     * @param updatingSavings the new saving goal updates
     * @param user the user who owns it
     * @return a Success Message if Updated
     */
    @Transactional
    public String updateSavings(Long Id, Savings updatingSavings, User user) {
        log.info("Validating the updated savings {}", updatingSavings);
        validateSavings(updatingSavings);

        Savings goalToUpdate = findSavingsByIdAndUser(Id, user);
        log.info("Savings updated with Idr: {}", goalToUpdate.getId());

        goalToUpdate.setGoalName(updatingSavings.getGoalName());
        goalToUpdate.setGoalDescription(updatingSavings.getGoalDescription());
        goalToUpdate.setTargetAmount(updatingSavings.getTargetAmount());

        savingsRepository.save(goalToUpdate);
        log.info("Savings updated with Idr: {}", goalToUpdate.getId());
        return "Savings updated successfully!";
    }

    /**
     * (If needed by user) Withdraw specific amount from a goal
     *
     * @param Id the goal ID to make the withdrawal
     * @param amount teh amount to withdrawal
     * @param user the user who owns the saving goal
     * @return a Success Message if Success to Withdrawal
     */
    @Transactional
    public String withdrawFromSavings(Long Id, BigDecimal amount, User user) {
        log.info("Withdrawing amount '{}' from saving goal Id {}", amount, Id);

        Savings goalToWithdrawFrom = findSavingsByIdAndUser(Id, user);
        goalToWithdrawFrom.withdrawFromSaving(amount);

        savingsRepository.save(goalToWithdrawFrom);
        log.info("Withdrawal amount '{}' from saving goal Id {}", amount, Id);
        return "Savings withdrawn successfully!";
    }

    /**
     * adds money to the goal
     * @param Id the goal id to add amount into
     * @param amount the amount needed to add
     * @param user the user who owns it
     * @return Success Message if amount gets deposited
     */
    @Transactional
    public String depositFromSavings(Long Id, BigDecimal amount, User user) {
        log.info("Depositing amount '{}' from saving goal Id {}", amount, Id);

        Savings goalToDeposit = findSavingsByIdAndUser(Id, user);
        goalToDeposit.addToSavings(amount);

        savingsRepository.save(goalToDeposit);
        log.info("Depositing amount '{}' from saving goal Id {}", amount, Id);
        return "Savings deposited successfully!";
    }

    /**
     * Deletes a goal for a user by the goal ID
     *
     * @param Id the goal ID to search
     * @param user the user whoo owns the goal
     * @return Success Message if goal was deleted
     */
    @Transactional
    public String deleteSavings(Long Id, User user) {
        log.info("Deleting saving Idc {} for user: {}", Id, user.getUsername());

        Savings goalToDelete = findSavingsByIdAndUser(Id, user);
        savingsRepository.delete(goalToDelete);

        log.info("Savings goal deleted successfully! {}", goalToDelete);
        return "Savings deleted successfully!";
    }

    /**
     * Gets the total savings for a user
     * @param user the user to fetch the savings
     * @return BigDecimal value, Total savings for a user
     */
    public BigDecimal getTotalSavings(User user) {
        log.info("Getting all savings for user: {}", user.getUsername());

        List<Savings> goals = savingsRepository.findByUser(user);

        return goals.stream()
                .map(Savings::getCurrentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Gets all Savings Goals for status -> In Progress
     * @param user the user who wants to find the goals by status
     * @return List of savings goal with status In-Progress
     */
    public List<Savings> getSavingsInProgress(User user) {
        log.info("Getting all savings for user: {} with status '{}'", user.getUsername(), Savings.GoalStatus.IN_PROGRESS);
        return savingsRepository.findByUserAndStatus(user,  Savings.GoalStatus.IN_PROGRESS);

    }

    /**
     * Gets all Savings Goals for status -> Completed
     * @param user the user who wants to find the goals by status
     * @return List of savings goal with status Completed
     */
    public List<Savings> getSavingsCompleted(User user) {
        log.info("Getting all savings for user: {} with status '{}'", user.getUsername(),  Savings.GoalStatus.COMPLETED);
        return savingsRepository.findByUserAndStatus(user,  Savings.GoalStatus.COMPLETED);
    }

    /**
     * Validates the Savings goals
     *
     * @param savings the savings Object to validate
     * @Throws ValidationException if the saving goal is not a valid request
     */
    private void validateSavings(Savings savings) {
        if (savings.getGoalName() == null || savings.getGoalName().trim().isEmpty())
            throw new ValidationException("Goal name is cannot be empty");
        if (savings.getGoalDescription() == null || savings.getGoalDescription().trim().isEmpty())
            throw new ValidationException("Goal description is cannot be empty");
        if (savings.getTargetAmount() == null || savings.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Target must be a positive digit");
        // current amount can be set if user wants to
        if (savings.getCurrentAmount() == null || savings.getCurrentAmount().compareTo(BigDecimal.ZERO) <0)
            throw new ValidationException("Current amount cannot be negative OR Empty");
    }
}
