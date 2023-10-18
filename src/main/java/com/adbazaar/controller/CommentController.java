package com.adbazaar.controller;

import com.adbazaar.dto.comment.UserBookComment;
import com.adbazaar.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Tag(name = "Comments Management")
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<UserBookComment>> getAllUserComments(@RequestParam("userId") Long id) {
        return ResponseEntity.ok(commentService.findAllUserCommentsById(id));
    }
}
