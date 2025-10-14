package com.rayyan.finance_tracker.controllers;

import com.rayyan.finance_tracker.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for user-related operations
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    /**
     * Fetches the authenticated user's profile information
     *
     * @param authentication the authenticated user's details
     * @return Response Entity with user profile data (username and email)
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, String>> getUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Map<String, String> profile = new HashMap<>();
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());

        return ResponseEntity.ok(profile);
    }
}

