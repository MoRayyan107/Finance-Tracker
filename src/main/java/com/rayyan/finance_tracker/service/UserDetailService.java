package com.rayyan.finance_tracker.service;

import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * This method is used in Spring Security during the authentication process
     * This method will find the user by its given username
     *
     * @param username To find the username from the Database
     * @return The UserDetail Object for the username if found
     * @throws UsernameNotFoundException if the username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username)) // allow login with email as well 
                .orElseThrow(() -> new UsernameNotFoundException("Username Not Found: "+username));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .or(()-> userRepository.findByEmail(username)) // allow login with email as well
                .orElseThrow(() -> new UsernameNotFoundException("Username Not Found: "+username));
    }
}
