package com.adbazaar.controller;

import com.adbazaar.dto.ApiError;
import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.book.BookUpdate;
import com.adbazaar.dto.book.NewBook;
import com.adbazaar.dto.comment.NewComment;
import com.adbazaar.dto.user.UserDetails;
import com.adbazaar.service.BookService;
import com.adbazaar.service.CommentService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    private final BookService bookService;

    private final CommentService commentService;

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
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(userId, productDetails));
    }

    @DeleteMapping("/{userId}/books/{bookId}")
    public ResponseEntity<ApiResp> deleteById(@PathVariable("userId") Long userId,
                                              @PathVariable("bookId") Long bookId,
                                              @RequestHeader(AUTHORIZATION) String token) {
        return ResponseEntity.ok(bookService.deleteBookById(userId, bookId, token));
    }

    @PatchMapping("/{userId}/books/{bookId}")
    public ResponseEntity<ApiResp> updateById(@PathVariable("userId") Long userId,
                                              @PathVariable("bookId") Long bookId,
                                              @RequestHeader(AUTHORIZATION) String token,
                                              @RequestBody BookUpdate details) {
        return ResponseEntity.ok(bookService.updateById(userId, bookId, details, token));
    }

    @PostMapping("/{userId}/books/{bookId}/comments")
    public ResponseEntity<ApiResp> addComment(@PathVariable("userId") Long userId,
                                              @PathVariable("bookId") Long bookId,
                                              @Valid @RequestBody NewComment comment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(userId, bookId, comment));
    }

    @Operation(
            summary = "Add a book into favorites list",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "User add book to his favorites list",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResp.class))}),
                    @ApiResponse(responseCode = "400",
                            description = "Validation failed",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "404",
                            description = "User/Book not found",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "409",
                            description = "Book already in a list",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
            }
    )
    @PostMapping("/{id}/favorites")
    public ResponseEntity<ApiResp> addBookToFavorites(@PathVariable("id") Long userId,
                                                      @RequestParam("bookId") Long bookId) {
        return ResponseEntity.ok(bookService.addToUserFavorites(userId, bookId));
    }

    @Operation(
            summary = "Add a book into orders list",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "User add book to his orders list",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResp.class))}),
                    @ApiResponse(responseCode = "400",
                            description = "Validation failed",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "404",
                            description = "User/Book not found",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "409",
                            description = "Book already in a list",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
            }
    )
    @PostMapping("/{id}/orders")
    public ResponseEntity<ApiResp> addBookToOrders(@PathVariable("id") Long userId,
                                                   @RequestParam("bookId") Long bookId) {
        return ResponseEntity.ok(bookService.addToUserOrders(userId, bookId));
    }
}
