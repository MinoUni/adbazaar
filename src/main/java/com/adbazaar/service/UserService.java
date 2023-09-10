package com.adbazaar.service;

import com.adbazaar.dto.LoginRequest;
import com.adbazaar.dto.LoginResponse;
import com.adbazaar.dto.RegistrationRequest;
import com.adbazaar.dto.RegistrationResponse;
import com.adbazaar.enums.Role;
import com.adbazaar.exception.UserAlreadyExistException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.model.AppUser;
import com.adbazaar.repository.UserRepository;
import com.adbazaar.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public RegistrationResponse register(RegistrationRequest userDetails) {
        if (userRepo.existsByEmail(userDetails.getEmail())) {
            throw new UserAlreadyExistException(String.format("User with email %s already exist", userDetails.getEmail()));
        }
        var user = AppUser.builder()
                .fullName(userDetails.getFullName())
                .email(userDetails.getEmail())
                .password(passwordEncoder.encode(userDetails.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        userRepo.save(user);
        return RegistrationResponse.builder()
                .email(userDetails.getEmail())
                .build();
    }

    public LoginResponse login(LoginRequest userDetails) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails.getEmail(), userDetails.getPassword()));
        var user = userRepo.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found", userDetails.getEmail())));
        return LoginResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .accessToken(jwtService.generateAccessToken(user))
                .build();
    }
}
