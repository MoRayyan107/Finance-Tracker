package com.rayyan.finance_tracker.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
 * This is a dedicated Service for handling JSON Web Tokens (JWT)
 * this includes creation, parsing and validating tokens
 */
@Service
public class JwtService {

    // injects the secret key stored in the application
    // best practice to store in a separate file than a hardcoded version
    @Value("${application.security.jwt.prod.secret-key}")
    private String SECRET_KEY;

    /**
     * Extracts Username form a given JWT token.
     * The Username is stored in Subject "claim" of the token
     *
     * @param jwtToken The token to extract the username form.
     * @return The username (Subject) from the token
     */
    public String extractUsername(String jwtToken){
        return extractClaim(jwtToken, Claims::getSubject);
    }

    /**
     * This method generates a random token for each user
     * with a set expiration time of 24hrs for each user
     *
     * @param userDetails the userDetails to pass into generating tokens
     * @return The generated JWT token
     */
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * This method generates a random token for each user
     * with a set expiration time of 24hrs for each user
     *
     * @param extraClaims The claims that are needed to set for a user
     * @param userDetails The userDetails to store in the claims
     * @return The generated JWT token
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 1440))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * This method validates the JWT if the Username matches and is not expired
     *
     * @param jwtToken The jwt token to check its validity
     * @param userDetails the user to validate against the jwt token
     * @return a boolean value if its valid token
     */
    public boolean isTokenValid(String jwtToken, UserDetails userDetails){
        try {
            final String usename = extractUsername(jwtToken);
            return usename.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken);
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    /**
     * This method checks if the given token is with the Expiration timeline
     *
     * @param jwtToken the token to check its expiration validity
     * @return a boolean value if the token is withing its expiration timeline
     */
    private boolean isTokenExpired(String jwtToken){
        return extractClaim(jwtToken, Claims::getExpiration).before(new Date());
    }

    /**
     * A generic method to extract a specific claim by JWT
     *
     * @param jwtToken The JWT token to extract the Claims from
     * @param claimsResolver This denotes which Claims to extract (eg: Expiration, subject, etc.)
     * @param <T> a generic way to denote a Data Type (eg: String, int, float, etc.)
     * @return The value of the requested claim
     */
    private <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    /**
     * The critical parsing method.
     * This method Validates the JWT token signature using the SECRET_KEY
     * and extracts the entire claim (Payload)
     *
     * @param jwtToken The JWT token to extract the Claims
     * @return The "claims" object containing the tokens Payload
     */
    private Claims extractAllClaims(String jwtToken){
        return Jwts
                .parserBuilder()
                // sets the SECRET_KEY for verifying the token signature
                .setSigningKey(getSignKey())
                .build()
                // this method both passes and validates the token signature
                // if failed to validate, throws an Exception
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    /**
     * Decodes the BASE64 encoded secret key from the stored file
     * converts into a cryptographical Key Object suitable for HS256 Algorithm
     *
     * @return a Key object for signing and validating JWTs
     */
    private Key getSignKey() {
        // decodes the SECRET_KEY from BASE64 format into a byte array
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        // create a secure Key object from the byte array for HMAC-SHA algorithm
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
