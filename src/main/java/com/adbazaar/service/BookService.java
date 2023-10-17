package com.adbazaar.service;

import com.adbazaar.dto.book.BookDetails;
import com.adbazaar.dto.book.BookShortDetails;
import com.adbazaar.exception.BookNotFoundException;
import com.adbazaar.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepo;

    public BookDetails findById(Long id) {
        var book = bookRepo.findBookById(id)
                .orElseThrow(() -> new BookNotFoundException(String.format("Book with id {%d} not found", id)));
        book.setComments(bookRepo.findAllBookComments(id));
        return book;
    }

    public Page<BookShortDetails> findAll(Pageable pageable) {
        return bookRepo.findAllBy(pageable);
    }

}
