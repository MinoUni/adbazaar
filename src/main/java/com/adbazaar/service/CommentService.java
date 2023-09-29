package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.comment.CommentDetails;
import com.adbazaar.dto.comment.NewComment;
import com.adbazaar.exception.ProductNotFoundException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.model.Comment;
import com.adbazaar.repository.CommentRepository;
import com.adbazaar.repository.ProductRepository;
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
    private final ProductRepository productRepo;

    public ApiResp create(NewComment commentDetails) {
        var user = userRepo.findById(commentDetails.getUserId())
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id {%d} not found", commentDetails.getUserId())));
        var product = productRepo.findById(commentDetails.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product with id {%d} not found", commentDetails.getProductId())));
        var comment = Comment.build(commentDetails, user, product);
        commentRepo.save(comment);
        return ApiResp.builder()
                .status(HttpStatus.CREATED.value())
                .message("Comment created")
                .build();
    }

    public List<CommentDetails> findCommentsByUserId(Long id) {
        if (!userRepo.existsById(id)) {
            throw new UserNotFoundException(String.format("User with id {%d} not found", id));
        }
        return commentRepo.findAllByUserId(id);
    }

    public List<CommentDetails> findCommentsByProductId(Long id) {
        if (!productRepo.existsById(id)) {
            throw new ProductNotFoundException(String.format("User with id {%d} not found", id));
        }
        return commentRepo.findAllByProductId(id);
    }
}
