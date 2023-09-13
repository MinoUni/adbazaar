package com.adbazaar.service;

import com.adbazaar.dto.ApiResponse;
import com.adbazaar.dto.LoginRequest;
import com.adbazaar.dto.LoginResponse;
import com.adbazaar.dto.RegistrationRequest;
import com.adbazaar.dto.RegistrationResponse;
import com.adbazaar.dto.UserVerification;
import com.adbazaar.enums.Role;
import com.adbazaar.exception.AccountVerificationException;
import com.adbazaar.exception.UserAlreadyExistException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.model.AppUser;
import com.adbazaar.model.VerificationCode;
import com.adbazaar.repository.UserRepository;
import com.adbazaar.repository.VerificationCodeRepository;
import com.adbazaar.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

/*
 *  TODO:[OPTIONAL]:Think about replace Roles(String) with Roles(List<Authority>)
 *   1. Refuse access to some functionality if lack of authorities or provide only part of functionality
 *  TODO:Complete verification process, move reset and change password functions, add base user management endpoints
 *   1. Deni access if unverified
 *   2. Send email with code
 *   3. Code validation(!isExpired)
 *   4. Change user status if verification successful
 *   5. Reassign and resend verification code if previous expired
 *
 * */
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeRepository verificationCodeRepo;

    public RegistrationResponse register(RegistrationRequest userDetails) {
        if (userRepo.existsByEmail(userDetails.getEmail())) {
            throw new UserAlreadyExistException(String.format("User with email %s already exist", userDetails.getEmail()));
        }
        var user = AppUser.builder()
                .fullName(userDetails.getFullName())
                .email(userDetails.getEmail())
                .password(passwordEncoder.encode(userDetails.getPassword()))
                .role(Role.ROLE_USER)
                .isVerified(Boolean.FALSE)
                .creationDate(LocalDate.now())
                .build();
        user.setVerificationCode(assignVerificationCode(user));
        userRepo.save(user);
        return RegistrationResponse.builder()
                .email(userDetails.getEmail())
                .build();
    }

    public LoginResponse login(LoginRequest userDetails) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails.getEmail(), userDetails.getPassword()));
        var user = findUser(userDetails.getEmail());
        return LoginResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.assignRefreshToken(user))
                .build();
    }

    public ApiResponse verifyCode(UserVerification userDetails) {
        var user = findUser(userDetails.getEmail());
        if (user.getIsVerified()) {
            throw new AccountVerificationException("Account already verified");
        }
        var now = LocalDateTime.now();
        if (!now.isBefore(user.getVerificationCode().getExpirationDate())) {
            throw new AccountVerificationException("Code expired");
        }
        if (!Objects.equals(userDetails.getVerificationCode(), user.getVerificationCode().getCode())) {
            throw new AccountVerificationException("Invalid code");
        }
        var userVerificationCode = user.getVerificationCode();
        userVerificationCode.revoke();
        verificationCodeRepo.delete(userVerificationCode);
        user.setIsVerified(Boolean.TRUE);
        userRepo.save(user);
        return ApiResponse.builder()
                .timestamp(now)
                .status(HttpStatus.OK.value())
                .message("Account verification successful")
                .build();
    }

    private VerificationCode assignVerificationCode(AppUser user) {
        return VerificationCode.builder()
                .user(user)
                .code(String.format("%04d", new Random().nextInt(10_000)))
                .expirationDate(LocalDateTime.now().plusHours(1L))
                .build();
    }

    private AppUser findUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found", email)));
    }
}
