package com.rayyan.finance_tracker.repository;

import com.rayyan.finance_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // gets the UserDetails from the username
    Optional<User> findByUsername(String username);

    // get the userDetails by email
    Optional<User> findByEmail(String email);
}
