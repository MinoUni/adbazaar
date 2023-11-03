package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.book.BookDetails;
import com.adbazaar.dto.book.BookShortDetails;
import com.adbazaar.dto.book.BookUpdate;
import com.adbazaar.dto.book.NewBook;
import com.adbazaar.exception.BookException;
import com.adbazaar.exception.BookNotFoundException;
import com.adbazaar.exception.CloudinaryUploadException;
import com.adbazaar.model.Book;
import com.adbazaar.repository.BookRepository;
import com.adbazaar.utils.CustomMapper;
import com.adbazaar.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    private final CloudinaryService cloudinaryService;

    public ApiResp create(Long userId, String token, MultipartFile file, NewBook newBook) {
        if (file == null) {
            throw new CloudinaryUploadException("No file to upload");
        }
        var user = serviceUtils.validateThatSameUserCredentials(userId, token);
        if (bookRepo.existsByParams(newBook.getTitle(), newBook.getAuthor(), newBook.getFormat(),
                newBook.getGenre(), newBook.getLanguage(), newBook.getPublishHouse())) {
            throw new BookException(BOOK_ALREADY_EXISTS);
        }
        String imageUrl = cloudinaryService.uploadBookImage(file, String.format("%s_%s_%s_%s", newBook.getTitle(), newBook.getFormat(),
                newBook.getPublishHouse(), newBook.getLanguage()));
        var book = Book.build(newBook, imageUrl, user);
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

    public ApiResp updateById(Long userId, Long bookId, String token, BookUpdate details, MultipartFile file) {
        if (file == null) {
            throw new CloudinaryUploadException("No file to upload");
        }
        var user = serviceUtils.validateThatSameUserCredentials(userId, token);
        var book = serviceUtils.findBookById(bookId);
        if (!user.getBooks().contains(book)) {
            throw new BookNotFoundException(String.format(BOOK_NOT_FOUND_IN_USER_BOOKS_LIST, userId, bookId));
        }
        String imageUrl = cloudinaryService.uploadBookImage(file, String.format("%s_%s_%s_%s", details.getTitle(), details.getFormat(),
                details.getPublishHouse(), details.getLanguage()));
        book.setImagePath(imageUrl);
        mapper.updateBook(details, book);
        bookRepo.save(book);
        return ApiResp.build(HttpStatus.OK, String.format(BOOK_UPDATED, userId, bookId));
    }

    public Page<BookShortDetails> findAll(Pageable pageable) {
        return bookRepo.findAllBy(pageable);
    }

}
