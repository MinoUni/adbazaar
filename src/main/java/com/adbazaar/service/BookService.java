package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.book.BookDetails;
import com.adbazaar.dto.book.BookShortDetails;
import com.adbazaar.dto.book.BookUpdate;
import com.adbazaar.dto.book.NewBook;
import com.adbazaar.exception.BookException;
import com.adbazaar.exception.BookNotFoundException;
import com.adbazaar.model.Book;
import com.adbazaar.repository.BookRepository;
import com.adbazaar.utils.CustomMapper;
import com.adbazaar.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.adbazaar.utils.MessageUtils.BOOK_ALREADY_EXISTS;
import static com.adbazaar.utils.MessageUtils.BOOK_CREATED;
import static com.adbazaar.utils.MessageUtils.BOOK_DELETED;
import static com.adbazaar.utils.MessageUtils.BOOK_NOT_FOUND_IN_USER_BOOKS_LIST;
import static com.adbazaar.utils.MessageUtils.BOOK_UPDATED;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepo;

    private final CustomMapper mapper;

    private final ServiceUtils serviceUtils;

    public ApiResp create(Long userId, String token, NewBook newBook) {
        var user = serviceUtils.validateThatSameUserCredentials(userId, token);
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

    public ApiResp deleteBookById(Long userId, Long bookId, String token) {
        var user = serviceUtils.validateThatSameUserCredentials(userId, token);
        var book = serviceUtils.findBookById(bookId);
        if (!user.getBooks().contains(book)) {
            throw new BookNotFoundException(String.format(BOOK_NOT_FOUND_IN_USER_BOOKS_LIST, userId, bookId));
        }
        bookRepo.delete(book);
        return ApiResp.build(HttpStatus.OK, String.format(BOOK_DELETED, userId, bookId));
    }

    public ApiResp updateById(Long userId, Long bookId, BookUpdate details, String token) {
        var user = serviceUtils.validateThatSameUserCredentials(userId, token);
        var book = serviceUtils.findBookById(bookId);
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

}
