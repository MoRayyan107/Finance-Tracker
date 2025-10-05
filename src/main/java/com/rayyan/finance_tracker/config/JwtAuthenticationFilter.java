package com.rayyan.finance_tracker.config;

import com.rayyan.finance_tracker.service.UserDetailService;
import com.rayyan.finance_tracker.service.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailService userDetails;
    private final JwtService jwtService;

    /**
     * Filter to authenticate JWT token in the request
     * 
     * @param request httpServletRequest
     * @param response httpServletResponse
     * @param filterChain filterChain object
     * @throws ServletException if a servlet exception occurs
     * @throws IOException if an I/O exception occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
            ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Step 1: check if the header is null OR starts with "Bearer"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // pass the request to the next filter chain
        }
        // Step 2: Extract the auth JWT token from the header
        jwt = authHeader.substring(7); // "Bearer " length = 7

        // Step 3: extract the username by providing the token
        username = jwtService.extractUsername(jwt);

        // Step 4: Check if user is NOT null and the user IS validated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // get the user details from the database
            UserDetails userDetails = this.userDetails.loadUserByUsername(username);
            // Step 5: check if the JWT token is valid
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // if the token is valid, create a new authentication Object for Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // adds the original HTTP request into the Authentication object
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                /* Step 6: The crucial step, the Security Context Holder needs to be updated
                 * this marks the current user as authenticated for the duration of that request
                 * the next filters will mark this as the User is logged in
                */
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Step 7: pass the request and response along to the next filter chain
        filterChain.doFilter(request, response);
    }
}
