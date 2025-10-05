package com.rayyan.finance_tracker.service.authentication;

import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.entity.authentication.AuthenticationRequest;
import com.rayyan.finance_tracker.entity.authentication.AuthenticationResponse;
import com.rayyan.finance_tracker.entity.authentication.RegisterRequest;
import com.rayyan.finance_tracker.repository.UserRepository;
import com.rayyan.finance_tracker.service.jwt.JwtService;
import com.rayyan.finance_tracker.utils.ValidatingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.rayyan.constants.Constants.MAX_USERNAME_LENGTH;
import static com.rayyan.constants.Constants.MIN_USERNAME_LENGTH;

import static com.rayyan.constants.Constants.MAX_PASSWORD_LENGTH;
import static com.rayyan.constants.Constants.MIN_PASSWORD_LENGTH;

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
        isValidRequest(request.getUsername(), request.getPassword());

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
        isValidRequest(request.getUsername(), request.getPassword());

        // code for authenticating an existing account
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                ));

        // get the User from the database
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        // generate a new jwt token for that user
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    // ----------------------- Validation Request Check ---------------------------

    /**
     * This method is used for checking if the username and password are NULL or Empty
     * and checks their minimum Length size 8.
     *
     * If the fields are empty or null and size to be less than minLength
     * Then throws an Exception
     *
     * @param username The username passed by Register/Login Request
     * @param password The password passed by Register/Login Request
     */
    private void isValidRequest(String username, String password) {
        List<String> exceptions  = new ArrayList<>();

        // check if the entered credentials are not null OR empty
        ValidatingUtil.checkIsEmpty(username,"Username",exceptions);
        ValidatingUtil.checkIsEmpty(password,"Password",exceptions);
        // check the length of the username and password
        ValidatingUtil.checkMinLength(username,"Username",MIN_USERNAME_LENGTH, exceptions);
        ValidatingUtil.checkMinLength(password,"Password",MIN_PASSWORD_LENGTH, exceptions);
        // check the length of the username and password IF EXCEEDS
        ValidatingUtil.checkMaxLength(username,"Username",MAX_USERNAME_LENGTH, exceptions);
        ValidatingUtil.checkMaxLength(password,"Password",MAX_PASSWORD_LENGTH, exceptions);

        // throw the ValidationException if any errors exists
        ValidatingUtil.throwIfExists(exceptions);
    }
}
