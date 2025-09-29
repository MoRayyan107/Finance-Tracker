package com.rayyan.finance_tracker.service;

import org.springframework.security.core.userdetails.User;
import com.rayyan.finance_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * @param username
     * @return new User object
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            com.rayyan.finance_tracker.entity.User user = userRepository.findByUsername(username);
            return new User(user.getUsername(), passwordEncoder.encode(user.getPassword()), new ArrayList<>());
        }catch(Exception e){
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
