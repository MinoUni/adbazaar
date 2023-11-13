package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.comment.NewComment;
import com.adbazaar.dto.comment.UserBookComment;
import com.adbazaar.exception.BookException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.model.Book;
import com.adbazaar.model.Comment;
import com.adbazaar.repository.BookRepository;
import com.adbazaar.repository.CommentRepository;
import com.adbazaar.repository.UserRepository;
import com.adbazaar.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.adbazaar.utils.MessageUtils.BOOK_RATE_COUNT_FAIL;
import static com.adbazaar.utils.MessageUtils.USER_ADD_COMMENT_TO_A_BOOK;
import static com.adbazaar.utils.MessageUtils.USER_NOT_FOUND_BY_ID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepo;

    private final UserRepository userRepo;

    private final BookRepository bookRepo;

    private final ServiceUtils serviceUtils;

    public ApiResp create(Long userId, Long bookId, String token, NewComment commentDetails) {
        var user = serviceUtils.validateThatSameUserCredentials(userId, token);
        var book = serviceUtils.findBookById(bookId);
        var comment = Comment.build(commentDetails, user, book);
        commentRepo.save(comment);
        countBookRate(book);
        return ApiResp.build(HttpStatus.CREATED, USER_ADD_COMMENT_TO_A_BOOK);
    }

    public List<UserBookComment> findAllUserComments(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_BY_ID, userId));
        }
        return commentRepo.findAllByUserId(userId);
    }

    private void countBookRate(Book book) {
        double avg = book.getComments()
                .stream()
                .mapToDouble(Comment::getRate)
                .average()
                .orElseThrow(() -> new BookException(BOOK_RATE_COUNT_FAIL));
        book.setRate(avg);
        bookRepo.save(book);
    }

}
