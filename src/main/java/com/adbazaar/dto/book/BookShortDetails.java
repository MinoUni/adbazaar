package com.adbazaar.dto.book;

import com.adbazaar.model.Book;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookShortDetails {

    private Long id;

    @JsonProperty("image_path")
    private String imagePath;

    private String title;

    private String author;

    private String format;

    private BigDecimal rate;

    private Integer quantity;

    private BigDecimal price;

    public static BookShortDetails build(Book book) {
        return BookShortDetails.builder()
                .id(book.getId())
                .imagePath(book.getImagePath())
                .title(book.getTitle())
                .author(book.getAuthor())
                .format(book.getFormat())
                .rate(book.getRate())
                .quantity(book.getQuantity())
                .price(book.getPrice())
                .build();
    }
}
