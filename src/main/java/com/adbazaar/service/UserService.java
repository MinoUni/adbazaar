package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.authentication.LoginRequest;
import com.adbazaar.dto.authentication.LoginResponse;
import com.adbazaar.dto.authentication.RegistrationRequest;
import com.adbazaar.dto.authentication.UserVerification;
import com.adbazaar.exception.AccountVerificationException;
import com.adbazaar.exception.UserAlreadyExistException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.model.AppUser;
import com.adbazaar.model.VerificationCode;
import com.adbazaar.repository.UserRepository;
import com.adbazaar.repository.VerificationCodeRepository;
import com.adbazaar.security.JwtService;
import com.adbazaar.utils.MailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeRepository verificationCodeRepo;
    private final JavaMailSender javaMailSender;
    private final String appEmail;

    public UserService(UserRepository userRepo,
                       VerificationCodeRepository verificationCodeRepo,
                       JwtService jwtService,
                       JavaMailSender javaMailSender,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       @Value("spring.mail.username") String applicationEmail) {
        this.userRepo = userRepo;
        this.verificationCodeRepo = verificationCodeRepo;
        this.jwtService = jwtService;
        this.javaMailSender = javaMailSender;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.appEmail = applicationEmail;
    }

    public ApiResp register(RegistrationRequest userDetails) {
        if (userRepo.existsByEmail(userDetails.getEmail())) {
            throw new UserAlreadyExistException(String.format("User with email %s already exist", userDetails.getEmail()));
        }
        var user = AppUser.build(userDetails);
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        user.setVerificationCode(assignVerificationCode(user));
        userRepo.save(user);
        sendEmail(MailDetails.builder()
                    .user(user)
                    .subject("Account code activation")
                    .content(getContentForVerificationCode(user))
                    .build());
        return ApiResp.builder()
                .status(HttpStatus.CREATED.value())
                .message(String.format("User created, verification code send to {%s}", userDetails.getEmail()))
                .build();
    }

    public LoginResponse login(LoginRequest userDetails) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails.getEmail(), userDetails.getPassword()));
        var user = findUser(userDetails.getEmail());
//      TODO: Replace with user details
        return LoginResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.assignRefreshToken(user))
                .build();
    }

    public ApiResp verifyCode(UserVerification userDetails) {
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
        return ApiResp.builder()
                .status(HttpStatus.OK.value())
                .message("Account verification successful")
                .build();
    }

    public ApiResp reassignVerificationCode(String email) {
        var user = findUser(email);
        if (user.getIsVerified()) {
            throw new AccountVerificationException("Account already verified");
        }
        var verCode = user.getVerificationCode();
        verCode.setCode(generateCode());
        verCode.setExpirationDate(LocalDateTime.now().plusHours(1L));
        verificationCodeRepo.save(verCode);
//        sendEmail(MailDetails.builder()
//                .user(user)
//                .subject("Account code activation")
//                .content(getContentForVerificationCode(user))
//                .build());
        return ApiResp.builder()
                .status(HttpStatus.OK.value())
                .message("Verification code reassigned")
                .build();
    }

    private void sendEmail(MailDetails mailDetails) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(appEmail, "AdBazaar");
            helper.setTo(mailDetails.getUser().getEmail());
            helper.setSubject(mailDetails.getSubject());
            message.setContent(mailDetails.getContent(), "text/html; charset=UTF-8");
            javaMailSender.send(message);
        } catch (MessagingException | MailException | UnsupportedEncodingException e) {
            throw new MailSendException(e.getMessage());
        }
    }

    private String getContentForVerificationCode(AppUser user) {
        var content = """
                Dear [[userFullName]]!<br>
                There is your account verification code:<br>
                <h3>[[verificationCode]]</h3><br>
                """;
        content = content.replace("[[userFullName]]", user.getFullName());
        content = content.replace("[[verificationCode]]", user.getVerificationCode().getCode());
        return content;
    }

    private VerificationCode assignVerificationCode(AppUser user) {
        return VerificationCode.builder()
                .user(user)
                .code(generateCode())
                .expirationDate(LocalDateTime.now().plusHours(1L))
                .build();
    }

    private static String generateCode() {
        return String.format("%04d", new Random().nextInt(10_000));
    }

    private AppUser findUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found", email)));
    }
}
