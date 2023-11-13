package com.adbazaar.controller;

import com.adbazaar.dto.ApiError;
import com.adbazaar.dto.comment.UserBookComment;
import com.adbazaar.dto.user.UserDetails;
import com.adbazaar.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Tag(name = "Comments Management")
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Fetch all user comments",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "User comments fetched",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDetails.class))}),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))}),
                    @ApiResponse(responseCode = "404",
                            description = "User not found",
                            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
            }
    )
    @GetMapping
    public ResponseEntity<List<UserBookComment>> getAllUserComments(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(commentService.findAllUserComments(userId));
    }
}
