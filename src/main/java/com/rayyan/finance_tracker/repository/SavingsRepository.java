package com.rayyan.finance_tracker.repository;

import com.rayyan.finance_tracker.entity.Savings;
import com.rayyan.finance_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, Long> {
    // JPA Repositories does the queries

    // finds savings by username
    List<Savings> findByUser(User user);

    // finds Savings by email
    Optional<Savings> findByIdAndUser (Long id, User user);

    // gets the goals of the given transaction status
    List<Savings> findByUserAndStatus(User user, Savings.GoalStatus status);
}
