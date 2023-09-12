package com.adbazaar.controller;

import com.adbazaar.dto.LoginRequest;
import com.adbazaar.dto.LoginResponse;
import com.adbazaar.dto.RegistrationRequest;
import com.adbazaar.dto.RegistrationResponse;
import com.adbazaar.dto.RefreshTokenRequest;
import com.adbazaar.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.adbazaar.security.JwtService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(userDetails));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(userDetails));
    }

    @PostMapping("/token/revoke")
    public void revokeRefreshToken(@RequestBody RefreshTokenRequest refreshToken) {
        jwtService.revokeRefreshToken(refreshToken.getRefreshToken());
    }

}
