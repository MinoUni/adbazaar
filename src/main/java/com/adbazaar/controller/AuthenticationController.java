package com.adbazaar.controller;

import com.adbazaar.dto.ApiError;
import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.authentication.LoginRequest;
import com.adbazaar.dto.authentication.LoginResponse;
import com.adbazaar.dto.authentication.RefreshTokenRequest;
import com.adbazaar.dto.authentication.RegistrationRequest;
import com.adbazaar.dto.authentication.RegistrationResponse;
import com.adbazaar.dto.authentication.UserVerification;
import com.adbazaar.security.JwtService;
import com.adbazaar.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth management endpoints")
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;

    @Operation(
            summary = "Register a new user",
            description = "Create a new User object, generate 4-digit verification code and send it at provided email, save info into database",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "User created",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RegistrationResponse.class))}),
                    @ApiResponse(responseCode = "409",
                            description = "User with provided email already created",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "400",
                            description = "Validation failed",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
            }
    )
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(userDetails));
    }

    @Operation(
            summary = "Authenticate a created user",
            description = "Authenticate a user via basic auth(email + password) and generate JWT access & refresh tokens",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "User authenticated and JWT generated",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoginResponse.class))}),
                    @ApiResponse(responseCode = "404",
                            description = "User not found",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "400",
                            description = "Validation failed",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest userDetails) {
        return ResponseEntity.ok(userService.login(userDetails));
    }

    @Operation(
            summary = "Verify a created user account",
            description = "Verify a user via 4-digit code send at email",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "User authenticated and JWT generated",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResp.class))}),
                    @ApiResponse(responseCode = "404",
                            description = "User not found",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "400",
                            description = "Validation/Verification failed",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
            }
    )
    @PostMapping("/verification")
    public ResponseEntity<ApiResp> verifyUserCode(@Valid @RequestBody UserVerification userDetails) {
        return ResponseEntity.ok(userService.verifyCode(userDetails));
    }

    @Operation(
            summary = "Resend verification code",
            description = "Reassign and resend 4-digit verification code at provided email. Code valid 1 hour after creation",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Verification code reassigned and resend",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResp.class))}),
                    @ApiResponse(responseCode = "404",
                            description = "User not found",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "400",
                            description = "Validation/Verification failed",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
            }
    )
    @PostMapping("verification/resend")
    public ResponseEntity<ApiResp> resendVerifyCode(@Valid @RequestParam String email) {
        return ResponseEntity.ok(userService.reassignVerificationCode(email));
    }

    @Operation(
            summary = "Logout user",
            description = "Revoke user JWT refresh token to reject possibility to assign a new JWT access & refresh tokens pair",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "User JWT refresh token revoked",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResp.class))}),
                    @ApiResponse(responseCode = "404",
                            description = "Token not found",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "400",
                            description = "Validation failed",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
            }
    )
    @PostMapping("/token/revoke")
    public ResponseEntity<ApiResp> revokeRefreshToken(@RequestBody RefreshTokenRequest refreshToken) {
        return ResponseEntity.ok(jwtService.revokeRefreshToken(refreshToken.getRefreshToken()));
    }

}
