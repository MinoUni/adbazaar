package com.adbazaar.controller;

import com.adbazaar.dto.ApiError;
import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.book.NewBook;
import com.adbazaar.dto.user.UserDetails;
import com.adbazaar.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
            summary = "Find user details using JWT",
            description = "Find details about user in database by extracting claims from JWT access token",
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
    public ResponseEntity<UserDetails> getUserByJwt(@RequestHeader(AUTHORIZATION) String token) {
        return ResponseEntity.ok(userService.findUserDetailsByJwt(token));
    }

    @Operation(
            summary = "Create a new book",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "User create a new book",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResp.class))}),
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
    @PostMapping("/{id}/books")
    public ResponseEntity<ApiResp> addBook(@PathVariable("id") Long userId,
                                           @RequestBody NewBook productDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createBook(userId, productDetails));
    }

    @Operation(
            summary = "Add a book into favorites list",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "User add book to his favorites list",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResp.class))}),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "404",
                            description = "User/Book not found",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "400",
                            description = "Validation failed",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
            }
    )
    @PostMapping("/{id}/favorites")
    public ResponseEntity<ApiResp> addBookToFavorites(@PathVariable("id") Long userId,
                                                      @RequestParam("bookId") Long bookId) {
        return ResponseEntity.ok(userService.addBookToFavorites(userId, bookId));
    }
}
