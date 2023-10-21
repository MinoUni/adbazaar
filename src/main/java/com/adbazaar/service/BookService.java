package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.book.BookDetails;
import com.adbazaar.dto.book.BookShortDetails;
import com.adbazaar.dto.book.BookUpdate;
import com.adbazaar.dto.book.NewBook;
import com.adbazaar.exception.AccountVerificationException;
import com.adbazaar.exception.BookException;
import com.adbazaar.exception.BookNotFoundException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.exception.UserNotMatchWithJwtException;
import com.adbazaar.model.AppUser;
import com.adbazaar.model.Book;
import com.adbazaar.repository.BookRepository;
import com.adbazaar.repository.UserRepository;
import com.adbazaar.security.JwtService;
import com.adbazaar.utils.CustomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.adbazaar.utils.MessageUtils.BOOK_ALREADY_EXISTS;
import static com.adbazaar.utils.MessageUtils.BOOK_ALREADY_IN_FAVORITES;
import static com.adbazaar.utils.MessageUtils.BOOK_ALREADY_IN_ORDERS;
import static com.adbazaar.utils.MessageUtils.BOOK_CREATED;
import static com.adbazaar.utils.MessageUtils.BOOK_DELETED;
import static com.adbazaar.utils.MessageUtils.BOOK_NOT_FOUND_BY_ID;
import static com.adbazaar.utils.MessageUtils.BOOK_NOT_FOUND_IN_USER_BOOKS_LIST;
import static com.adbazaar.utils.MessageUtils.BOOK_UPDATED;
import static com.adbazaar.utils.MessageUtils.USER_ADD_TO_FAVORITES_OK;
import static com.adbazaar.utils.MessageUtils.USER_ADD_TO_ORDERS_OK;
import static com.adbazaar.utils.MessageUtils.USER_AND_SELLER_ARE_THE_SAME;
import static com.adbazaar.utils.MessageUtils.USER_NOT_FOUND_BY_ID;
import static com.adbazaar.utils.MessageUtils.USER_NOT_MATCH_WITH_JWT;
import static com.adbazaar.utils.MessageUtils.USER_NOT_VERIFIED;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepo;

    private final UserRepository userRepo;

    private final CustomMapper mapper;

    private final JwtService jwtService;

    public ApiResp create(Long userId, String token, NewBook newBook) {
        var user = validateThatSameUserCredentials(userId, token);
        if(bookRepo.existsByParams(newBook.getTitle(), newBook.getAuthor(), newBook.getFormat(),
                newBook.getGenre(), newBook.getLanguage(), newBook.getPublishHouse())) {
            throw new BookException(BOOK_ALREADY_EXISTS);
        }
        var book = Book.build(newBook, user);
        bookRepo.save(book);
        return ApiResp.build(HttpStatus.CREATED, String.format(BOOK_CREATED, userId));
    }

    public BookDetails findBookDetailsById(Long id) {
        var book = bookRepo.findBookDetailsById(id).orElseThrow();
        book.setComments(bookRepo.findAllBookComments(book.getId()));
        return book;
    }

    public ApiResp addToUserFavorites(Long userId, Long bookId, String token) {
        var user = validateThatSameUserCredentials(userId, token);
        var book = findBookById(bookId);
        if (user.getEmail().equals(book.getSeller().getEmail())) {
            throw new BookException(String.format(USER_AND_SELLER_ARE_THE_SAME, user.getEmail(), book.getSeller().getEmail()));
        }
        var userFavoriteBooks = user.getFavoriteBooks();
        if (userFavoriteBooks.contains(book)) {
            throw new BookException(String.format(BOOK_ALREADY_IN_FAVORITES, bookId, userId));
        }
        userFavoriteBooks.add(book);
        userRepo.save(user);
        return ApiResp.build(HttpStatus.OK, String.format(USER_ADD_TO_FAVORITES_OK, userId, bookId));
    }

    public ApiResp addToUserOrders(Long userId, Long bookId, String token) {
        var user = validateThatSameUserCredentials(userId, token);
        var book = findBookById(bookId);
        if (user.getEmail().equals(book.getSeller().getEmail())) {
            throw new BookException(String.format(USER_AND_SELLER_ARE_THE_SAME, user.getEmail(), book.getSeller().getEmail()));
        }
        var userOrders = user.getOrders();
        if (userOrders.contains(book)) {
            throw new BookException(String.format(BOOK_ALREADY_IN_ORDERS, bookId, userId));
        }
        userOrders.add(book);
        userRepo.save(user);
        return ApiResp.build(HttpStatus.OK, String.format(USER_ADD_TO_ORDERS_OK, userId, bookId));
    }

    public ApiResp deleteBookById(Long userId, Long bookId, String token) {
        var user = validateThatSameUserCredentials(userId, token);
        var book = findBookById(bookId);
        if (!user.getBooks().contains(book)) {
            throw new BookNotFoundException(String.format(BOOK_NOT_FOUND_IN_USER_BOOKS_LIST, userId, bookId));
        }
        bookRepo.delete(book);
        return ApiResp.build(HttpStatus.OK, String.format(BOOK_DELETED, userId, bookId));
    }

    public ApiResp updateById(Long userId, Long bookId, BookUpdate details, String token) {
        var user = validateThatSameUserCredentials(userId, token);
        var book = findBookById(bookId);
        if (!user.getBooks().contains(book)) {
            throw new BookNotFoundException(String.format(BOOK_NOT_FOUND_IN_USER_BOOKS_LIST, userId, bookId));
        }
        mapper.updateBook(details, book);
        bookRepo.save(book);
        return ApiResp.build(HttpStatus.OK, String.format(BOOK_UPDATED, userId, bookId));
    }

    public Page<BookShortDetails> findAll(Pageable pageable) {
        return bookRepo.findAllBy(pageable);
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

    private Book findBookById(Long bookId) {
        return bookRepo.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(String.format(BOOK_NOT_FOUND_BY_ID, bookId)));
    }

    private AppUser findUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_ID, id)));
    }
}
