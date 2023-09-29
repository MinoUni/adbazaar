package com.adbazaar.controller;

import com.adbazaar.dto.ApiResponse;
import com.adbazaar.dto.comment.CommentDetails;
import com.adbazaar.dto.comment.NewComment;
import com.adbazaar.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Tag(name = "Comments Management", description = "User comments API for products")
@RestController
@RequestMapping("/messages")
public class CommentController {

    private final CommentService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse> addComment(@Valid @RequestBody NewComment newComment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(newComment));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<CommentDetails>> getCommentsByUserId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(messageService.findCommentsByUserId(id));
    }
}
