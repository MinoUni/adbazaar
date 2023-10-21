package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.authentication.LoginRequest;
import com.adbazaar.dto.authentication.LoginResponse;
import com.adbazaar.dto.authentication.RegistrationRequest;
import com.adbazaar.dto.authentication.RegistrationResponse;
import com.adbazaar.dto.authentication.UserVerification;
import com.adbazaar.dto.user.UserDetails;
import com.adbazaar.exception.AccountVerificationException;
import com.adbazaar.exception.UserAlreadyExistException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.model.AppUser;
import com.adbazaar.model.VerificationCode;
import com.adbazaar.repository.BookRepository;
import com.adbazaar.repository.CommentRepository;
import com.adbazaar.repository.UserRepository;
import com.adbazaar.repository.UserVerifyTokenRepository;
import com.adbazaar.security.JwtService;
import com.adbazaar.utils.CustomMapper;
import com.adbazaar.utils.MailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.adbazaar.utils.MessageUtils.USER_ALREADY_EXIST;
import static com.adbazaar.utils.MessageUtils.USER_ALREADY_VERIFIED;
import static com.adbazaar.utils.MessageUtils.USER_NOT_FOUND_BY_EMAIL;
import static com.adbazaar.utils.MessageUtils.USER_VERIFICATION_CODE_EXPIRED;
import static com.adbazaar.utils.MessageUtils.USER_VERIFICATION_INVALID_CODE;
import static com.adbazaar.utils.MessageUtils.USER_VERIFICATION_REASSIGNED;
import static com.adbazaar.utils.MessageUtils.USER_VERIFICATION_SUCCESSFUL;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepo;

    private final BookRepository bookRepo;

    private final UserVerifyTokenRepository userVerifyTokenRepo;

    private final CommentRepository commentRepo;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final MailUtils mailUtils;

    private final CustomMapper mapper;

    public RegistrationResponse createUser(RegistrationRequest userDetails) {
        if (userRepo.existsByEmail(userDetails.getEmail())) {
            throw new UserAlreadyExistException(String.format(USER_ALREADY_EXIST, userDetails.getEmail()));
        }
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        var user = AppUser.build(userDetails);
        var code = userVerifyTokenRepo.save(user.getEmail(), VerificationCode.build(user.getEmail()));
        userRepo.save(user);
        log.info("==== CODE: {}  for User: {} ====", code.getCode(), user.getEmail());
//        mailUtils.sendEmail(user, code, VERIFICATION_MAIL_SUBJECT);
        return RegistrationResponse.builder().email(user.getEmail()).build();
    }

    public LoginResponse login(LoginRequest userDetails) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails.getEmail(), userDetails.getPassword()));
        var user = findUserByEmail(userDetails.getEmail());
        return LoginResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.assignRefreshToken(user))
                .build();
    }

    public ApiResp verifyCode(UserVerification userDetails) {
        var user = findUserByEmail(userDetails.getEmail());
        var code = userVerifyTokenRepo.findByEmail(user.getEmail());
        if (user.getIsVerified()) {
            throw new AccountVerificationException(String.format(USER_ALREADY_VERIFIED, user.getEmail()));
        }
        if (!LocalDateTime.now().isBefore(code.getExpirationDate())) {
            throw new AccountVerificationException(String.format(USER_VERIFICATION_CODE_EXPIRED, user.getEmail()));
        }
        if (!Objects.equals(userDetails.getVerificationCode(), code.getCode())) {
            throw new AccountVerificationException(String.format(USER_VERIFICATION_INVALID_CODE, user.getEmail()));
        }
        userVerifyTokenRepo.delete(user.getEmail());
        user.setIsVerified(Boolean.TRUE);
        userRepo.save(user);
        return ApiResp.build(HttpStatus.OK, String.format(USER_VERIFICATION_SUCCESSFUL, user.getEmail()));
    }

    public ApiResp reassignVerificationCode(String email) {
        var user = findUserByEmail(email);
        if (user.getIsVerified()) {
            throw new AccountVerificationException(String.format(USER_ALREADY_VERIFIED, user.getEmail()));
        }
        var code = userVerifyTokenRepo.save(user.getEmail(), VerificationCode.build(user.getEmail()));
        log.info("==== CODE REASSIGN: {}  for User: {} ====", code.getCode(), user.getEmail());
//        mailUtils.sendEmail(user, code, VERIFICATION_MAIL_SUBJECT);
        return ApiResp.build(HttpStatus.OK, String.format(USER_VERIFICATION_REASSIGNED, email));
    }

    public UserDetails findUserDetailsByJwt(String token) {
        var email = jwtService.extractUsernameFromAccessToken(token.substring(7));
        var user = userRepo.findUserDetailsByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL, email)));
        user.setComments(commentRepo.findAllUserComments(user.getId()));
        user.setBooks(bookRepo.findAllUserBooks(user.getId()));
        user.setFavorites(bookRepo.findAllUserFavoriteBooks(user.getId()));
        user.setOrders(bookRepo.findAllUserOrderedBooks(user.getId()));
        return user;
    }

    private AppUser findUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL, email)));
    }


}
