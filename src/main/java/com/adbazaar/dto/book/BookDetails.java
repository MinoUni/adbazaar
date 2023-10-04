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
@AllArgsConstructor
@NoArgsConstructor
public class BookDetails {

    private Long id;

    private String title;

    private String author;

    private String description;

    @JsonProperty("image_path")
    private String imagePath;

    private String format;

    private BigDecimal rate;

    private Integer quantity;

    private BigDecimal price;

    private String genre;

    @JsonProperty("publishing_house")
    private String publishHouse;

    private String language;

    private SellerDetails seller;

    public static BookDetails build(Book book) {
        return BookDetails.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .imagePath(book.getImagePath())
                .format(book.getFormat())
                .price(book.getPrice())
                .rate(book.getRate())
                .quantity(book.getQuantity())
                .genre(book.getGenre())
                .publishHouse(book.getPublishHouse())
                .language(book.getLanguage())
                .seller(SellerDetails.build(book.getSeller()))
                .build();
    }
}
