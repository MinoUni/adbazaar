package com.adbazaar.controller;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.book.BookUpdate;
import com.adbazaar.dto.book.NewBook;
import com.adbazaar.dto.book.BookDetails;
import com.adbazaar.dto.book.BookShortDetails;
import com.adbazaar.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Tag(name = "Books Management")
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<ApiResp> addNewProduct(@RequestHeader(AUTHORIZATION) String token,
                                                 @RequestBody NewBook productDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(productDetails, token));
    }

    @GetMapping
    public ResponseEntity<Page<BookShortDetails>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(bookService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetails> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResp> deleteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.deleteById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResp> updateById(@PathVariable("id") Long id,
                                              @RequestBody BookUpdate details) {
        return ResponseEntity.ok(bookService.update(id, details));
    }
}
