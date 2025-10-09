package com.rayyan.finance_tracker.config;

import com.rayyan.finance_tracker.service.UserDetailService;
import com.rayyan.finance_tracker.service.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailService userDetails;
    private final JwtService jwtService;

    @Autowired
    public JwtAuthenticationFilter(UserDetailService userDetails, JwtService jwtService) {
        this.userDetails = userDetails;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;

        // Step 1: Check Authorization header first
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // "Bearer " length = 7
        }
        // Step 2: If no Authorization header, check cookies
        else {
            jakarta.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        }

        // If no JWT found, continue without authentication
        if (jwt == null || jwt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: extract the username from token

        // Validate JWT format before parsing
        if (!isValidJwtFormat(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Invalid JWT, skip authentication
            filterChain.doFilter(request, response);
            return;
        }

        // Step 4: Validate token and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetails.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Checking for a valid token
     * @param token  the JWT token
     * @return true if valid
     */
    private boolean isValidJwtFormat(String token) {
        // JWT must contain exactly two periods
        if (token == null) return false;
        int periodCount = 0;
        for (char c : token.toCharArray()) {
            if (c == '.') periodCount++;
        }
        return periodCount == 2;
    }
}
