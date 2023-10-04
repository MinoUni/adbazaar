package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.book.NewBook;
import com.adbazaar.dto.book.BookDetails;
import com.adbazaar.dto.book.BookShortDetails;
import com.adbazaar.dto.book.BookUpdate;
import com.adbazaar.exception.BookNotFoundException;
import com.adbazaar.exception.UserNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepo;

    private final UserRepository userRepo;

    private final CustomMapper mapper;

    private final JwtService jwtService;

    public ApiResp create(NewBook productDetails, String token) {
        var email = jwtService.extractUsernameFromAccessToken(token.substring(7));
        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email {%s} not found", email)));
        var book = Book.build(productDetails, user);
        bookRepo.save(book);
        return ApiResp.builder()
                .status(HttpStatus.CREATED.value())
                .message("User %s add a new book")
                .build();
    }

    @Transactional(readOnly = true)
    public BookDetails findById(Long id) {
        return bookRepo.findById(id)
                .map(BookDetails::build)
                .orElseThrow(() -> new BookNotFoundException(String.format("Book with id {%d} not found", id)));
    }

    @Transactional(readOnly = true)
    public Page<BookShortDetails> findAll(Pageable pageable) {
        return bookRepo.findAllBy(pageable);
    }

    public ApiResp deleteById(Long id) {
        if (!bookRepo.existsById(id)) {
            throw new BookNotFoundException(String.format("Book with id {%d} not found", id));
        }
        bookRepo.deleteById(id);
        return ApiResp.builder()
                .status(HttpStatus.OK.value())
                .message(String.format("Book with id {%d} deleted", id))
                .build();
    }

    public ApiResp update(Long id, BookUpdate details) {
        var product = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException(String.format("Book with id {%d} not found", id)));
        mapper.updateBook(details, product);
        bookRepo.save(product);
        return ApiResp.builder()
                .status(HttpStatus.OK.value())
                .message(String.format("Book with id {%d} updated", id))
                .build();
    }


}
