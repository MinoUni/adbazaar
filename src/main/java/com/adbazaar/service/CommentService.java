package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.comment.NewComment;
import com.adbazaar.dto.comment.UserBookComment;
import com.adbazaar.exception.AccountVerificationException;
import com.adbazaar.exception.BookException;
import com.adbazaar.exception.BookNotFoundException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.exception.UserNotMatchWithJwtException;
import com.adbazaar.model.AppUser;
import com.adbazaar.model.Book;
import com.adbazaar.model.Comment;
import com.adbazaar.repository.BookRepository;
import com.adbazaar.repository.CommentRepository;
import com.adbazaar.repository.UserRepository;
import com.adbazaar.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.adbazaar.utils.MessageUtils.BOOK_NOT_FOUND_BY_ID;
import static com.adbazaar.utils.MessageUtils.BOOK_RATE_COUNT_FAIL;
import static com.adbazaar.utils.MessageUtils.USER_ADD_COMMENT_TO_A_BOOK;
import static com.adbazaar.utils.MessageUtils.USER_NOT_FOUND_BY_ID;
import static com.adbazaar.utils.MessageUtils.USER_NOT_MATCH_WITH_JWT;
import static com.adbazaar.utils.MessageUtils.USER_NOT_VERIFIED;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepo;

    private final UserRepository userRepo;

    private final BookRepository bookRepo;

    private final JwtService jwtService;

    public ApiResp create(Long userId, Long bookId, String token, NewComment commentDetails) {
        var user = validateThatSameUserCredentials(userId, token);
        var book = findBookById(bookId);
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

    private AppUser validateThatSameUserCredentials(Long userId, String token) {
        var user = findUserById(userId);
        if (!user.getIsVerified()) {
            throw new AccountVerificationException(String.format(USER_NOT_VERIFIED, user.getEmail()));
        }
        var email = jwtService.extractUsernameFromAccessToken(token.substring(7));
        if (!user.getEmail().equals(email)) {
            throw new UserNotMatchWithJwtException(String.format(USER_NOT_MATCH_WITH_JWT, user.getEmail(), email));
        }
        return user;
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

    private Book findBookById(Long bookId) {
        return bookRepo.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(String.format(BOOK_NOT_FOUND_BY_ID, bookId)));
    }

    private AppUser findUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_ID, id)));
    }

}
