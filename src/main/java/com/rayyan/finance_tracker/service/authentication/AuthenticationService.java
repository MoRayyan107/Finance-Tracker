package com.rayyan.finance_tracker.service.authentication;

import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.entity.authentication.AuthenticationRequest;
import com.rayyan.finance_tracker.entity.authentication.AuthenticationResponse;
import com.rayyan.finance_tracker.entity.authentication.RegisterRequest;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.repository.UserRepository;
import com.rayyan.finance_tracker.service.jwt.JwtService;
import com.rayyan.finance_tracker.utils.ValidatingUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.rayyan.finance_tracker.constants.Constants.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;


    public AuthenticationResponse register(RegisterRequest request) {
        // --------- Check if the request is valid -------------
        isValidRequestForRegister(request.getUsername(),
                                  request.getEmail(), 
                                  request.getPassword());

        // build a user by Username and Encode Password
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER) // default role for a user
                .build();

        // save the user into the database
        userRepository.save(user);

        // generate the token
        var jwtToken = jwtService.generateToken(user);

        // return a new object of Authentication Response
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // --------- Check if the request is valid -------------
        isValidRequestForAuth(request.getUsername(), request.getPassword());

        // code for authenticating an existing account
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(), //c can be used as email as well
                    request.getPassword()
                ));

        // get the User from the database
        var user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername())) // allow login with email as well
                .orElseThrow();

        // generate a new jwt token for that user
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    // ----------------------- Validation Request Check ---------------------------

   /**
    * Method to validate the registration request 
    * which includes username and password

    * @param usernameORemail the username or email from the request
    * @param password the password from the request
    * @throws ValidationException if validation fails
    */
    private void isValidRequestForAuth(String usernameORemail, String password) {
        List<String> exceptions  = new ArrayList<>();
        

        // check if the entered credentials are not null OR empty
        ValidatingUtil.checkIsEmpty(usernameORemail,"Username/Email",exceptions);
        ValidatingUtil.checkIsEmpty(password,"Password",exceptions);
        // check the length of the username and password
        ValidatingUtil.checkMinLength(usernameORemail,"Username/Email",MIN_USERNAME_LENGTH, exceptions);
        ValidatingUtil.checkMinLength(password,"Password",MIN_PASSWORD_LENGTH, exceptions);
        // check the length of the username and password IF EXCEEDS
        ValidatingUtil.checkMaxLength(usernameORemail,"Username/Email",MAX_EMAIL_LENGTH, exceptions);
        ValidatingUtil.checkMaxLength(password,"Password",MAX_PASSWORD_LENGTH, exceptions);

        // throw the ValidationException if any errors exists
        ValidatingUtil.throwIfExists(exceptions);
    }

    /**
     * Method to validate the registration request
     * which includes username, email and password
     * 
     * @param username the username from the request
     * @param email the email from the request
     * @param password the password from the request
     * @throws ValidationException if validation fails
     */
    private void isValidRequestForRegister(String username,String email, String password) {
        List<String> exceptions  = new ArrayList<>(); // holds all exceptions, if it gets any

        // check if the entered credentials are not null OR empty
        ValidatingUtil.checkIsEmpty(username,"Username",exceptions);
        ValidatingUtil.checkIsEmpty(password,"Password",exceptions);
        ValidatingUtil.checkIsEmpty(email,"Email",exceptions);
        // check the length of the username and password
        ValidatingUtil.checkMinLength(username,"Username",MIN_USERNAME_LENGTH, exceptions);
        ValidatingUtil.checkMinLength(password,"Password",MIN_PASSWORD_LENGTH, exceptions);
        ValidatingUtil.checkMinLength(email,"Email",MIN_EMAIL_LENGTH, exceptions);
        // check the length of the username and password IF EXCEEDS
        ValidatingUtil.checkMaxLength(username,"Username",MAX_USERNAME_LENGTH, exceptions);
        ValidatingUtil.checkMaxLength(password,"Password",MAX_PASSWORD_LENGTH, exceptions);
        ValidatingUtil.checkMaxLength(email,"Email",MAX_EMAIL_LENGTH, exceptions);

        // Special check for email
        ValidatingUtil.checkEmailFormat(email,"Email",exceptions);
        // throw the ValidationException if any errors exists
        ValidatingUtil.throwIfExists(exceptions);
    }
}
