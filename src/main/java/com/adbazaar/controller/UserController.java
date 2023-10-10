package com.adbazaar.controller;

import com.adbazaar.dto.ApiError;
import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.user.UserDetails;
import com.adbazaar.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Tag(name = "Users", description = "Users management endpoints")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Find user details using JWT access token",
            description = "Find user details(lists of orders, favorites, comments and products) in database by extracting claims from JWT access token",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "User details fetched",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDetails.class))}),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "404",
                            description = "User not found",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "400",
                            description = "Validation failed",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
            }
    )
    @GetMapping("/token")
    public ResponseEntity<UserDetails> getUserByAccessToken(@RequestHeader(AUTHORIZATION) String token) {
        return ResponseEntity.ok(userService.findUserDetailsByAccessToken(token));
    }

    @PostMapping("/{id}/favorites")
    public ResponseEntity<ApiResp> addBookToFavorites(@PathVariable("id") Long userId, @RequestParam("bookId") Long bookId) {
        return ResponseEntity.ok(userService.addBookToFavorites(userId, bookId));
    }
}
