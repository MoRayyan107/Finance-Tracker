package com.rayyan.finance_tracker.startupTest;


import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitialiser implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if a user with the username 'user' already exists.
        // This prevents creating a duplicate user every time the app restarts.
        if (userRepository.findByUsername("user").isEmpty()) {
            // Create a new User object
            User testUser = new User();
            testUser.setUsername("user");
            // IMPORTANT: We must encode the password before saving it.
            // We are using the PasswordEncoder bean we created in ApplicationConfig.
            testUser.setPassword(passwordEncoder.encode("password"));

            // Save the new user to the database.
            userRepository.save(testUser);

            System.out.println("======================================================");
            System.out.println("CREATED TEST USER: username='user', password='password'");
            System.out.println("======================================================");
        }
    }
}
