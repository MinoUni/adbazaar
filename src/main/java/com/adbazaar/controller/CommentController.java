package com.adbazaar.controller;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.comment.ProductComment;
import com.adbazaar.dto.comment.NewComment;
import com.adbazaar.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Tag(name = "Comments Management")
@RestController
@RequestMapping("/messages")
public class CommentController {

    private final CommentService messageService;

    @PostMapping
    public ResponseEntity<ApiResp> addComment(@Valid @RequestBody NewComment newComment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(newComment));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<ProductComment>> getCommentsByUserId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(messageService.findCommentsByUserId(id));
    }
}
