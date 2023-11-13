package com.adbazaar.utils;

import com.adbazaar.exception.AccountVerificationException;
import com.adbazaar.exception.BookNotFoundException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.exception.UserNotMatchWithJwtException;
import com.adbazaar.model.AppUser;
import com.adbazaar.model.Book;
import com.adbazaar.repository.BookRepository;
import com.adbazaar.repository.UserRepository;
import com.adbazaar.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.adbazaar.utils.MessageUtils.*;

@RequiredArgsConstructor
@Component
public class ServiceUtils {

    private final UserRepository userRepo;

    private final BookRepository bookRepo;

    private final JwtService jwtService;

    public AppUser validateThatSameUserCredentials(Long userId, String token) {
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

    public Book findBookById(Long bookId) {
        return bookRepo.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(String.format(BOOK_NOT_FOUND_BY_ID, bookId)));
    }

    public AppUser findUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_ID, id)));
    }
}
