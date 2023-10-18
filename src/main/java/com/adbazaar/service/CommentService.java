package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.comment.NewComment;
import com.adbazaar.dto.comment.UserBookComment;
import com.adbazaar.exception.BookNotFoundException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.model.Comment;
import com.adbazaar.repository.BookRepository;
import com.adbazaar.repository.CommentRepository;
import com.adbazaar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepo;

    private final UserRepository userRepo;

    private final BookRepository bookRepo;

    public ApiResp create(Long userId, Long bookId, NewComment commentDetails) {
        var user = userRepo.findById(commentDetails.getUserId())
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id {%d} not found", commentDetails.getUserId())));
        var book = bookRepo.findById(commentDetails.getBookId())
                .orElseThrow(() -> new BookNotFoundException(String.format("Book with id {%d} not found", commentDetails.getBookId())));
        var comment = Comment.build(commentDetails, user, book);
        commentRepo.save(comment);
        return ApiResp.build(HttpStatus.CREATED, "Comment created");
    }

    public List<UserBookComment> findAllUserCommentsById(Long id) {
        if (!userRepo.existsById(id)) {
            throw new UserNotFoundException(String.format("User with id {%d} not found", id));
        }
        return commentRepo.findAllByUserId(id);
    }
}
