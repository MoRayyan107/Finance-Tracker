package com.rayyan.finance_tracker.controllers;

import com.rayyan.finance_tracker.entity.Savings;
import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.service.SavingsService;
import com.rayyan.finance_tracker.service.UserDetailService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/savings")
public class SavingsController {

    private final SavingsService savingsService;
    private final UserDetailService userDetailService;

    /**
     * Get the current authenticated user from the security context (Helper Method)
     *
     * @return User object of the currently authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userDetailService.getUserByUsername(username);
    }

    /**
     * Creates a new Savings for a user
     * @param savings the savings object to create
     * @return a Success Message if created
     */
    @PostMapping("/create")
    public String createSavings(@RequestBody Savings savings) {
        savings.setUser(getCurrentUser());

        return savingsService.createSavings(savings);
    }

    /**
     * gets a list of savings
     * @return a List of savings for a user
     */
    @GetMapping("/my-savings")
    public List<Savings> getAllSavings() {
        return savingsService.findAllSavings(getCurrentUser());
    }

    /**
     * Gets a savings for a user
     * @param id the savings Id to fetch
     * @return the savings object
     */
    @GetMapping("/my-savings/{id}")
    public Savings getSavingsById(@PathVariable Long id) {
        return savingsService.findSavingsByIdAndUser(id, getCurrentUser());
    }

    /**
     * Updates an existing savings
     * @param Id the savings Id to update
     * @param updatedSavings new changes that needs to make into existing savings
     * @return a Success Message if updated
     */
    @PutMapping("/update/{Id}")
    public String updateSavings(@PathVariable Long Id, @RequestBody Savings updatedSavings) {
        return savingsService.updateSavings(Id, updatedSavings, getCurrentUser());
    }

    /**
     * deposits money to savings
     * @param id the savings Id to deposit to
     * @param amount the amount to deposit
     * @return Success Message if deposited
     */
    @PostMapping("/{id}/deposit")
    public String depositIntoSavings(@PathVariable Long id, @RequestBody BigDecimal amount) {
        return savingsService.depositFromSavings(id, amount, getCurrentUser());
    }

    /**
     * withdrawals money from a saving
     * @param id the savings Id to withdrawal from
     * @param amount the amount to withdrawal
     * @return Success Message if withdrawal
     */
    @PostMapping("/{id}/withdraw")
    public String withdrawFromSavings(@PathVariable Long id, @RequestBody BigDecimal amount) {
        return  savingsService.withdrawFromSavings(id, amount, getCurrentUser());
    }

    /**
     * deletes a saving
     * @param id the saving Id that needs to be deleted
     * @return a Success Message if deleted
     */
    @DeleteMapping("/delete/{id}")
    public String deleteSavings(@PathVariable Long id) {
        return savingsService.deleteSavings(id, getCurrentUser());
    }

    /**
     * gets the total amount of all savings
     * @return the savings that user have
     */
    @GetMapping("/total-savings")
    public BigDecimal totalSavings() {
        return savingsService.getTotalSavings(getCurrentUser());
    }

    /**
     * gets goals based on status
     * @return List oif goals based on status -> In Progress
     */
    @GetMapping("/in-progress")
    public List<Savings> getInProgressSavings() {
        return savingsService.getSavingsInProgress(getCurrentUser());
    }

    /**
     * gets goals based on status
     * @return List oif goals based on status -> Completed
     */
    @GetMapping("/completed")
    public List<Savings> getCompletedSavings() {
        return savingsService.getSavingsCompleted(getCurrentUser());
    }

}
