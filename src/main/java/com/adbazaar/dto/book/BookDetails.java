package com.adbazaar.dto.book;

import com.adbazaar.dto.comment.BookComment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
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

    private List<BookComment> comments = new ArrayList<>();

    public BookDetails(Long id, String title, String author, String description, String imagePath, String format, BigDecimal rate, Integer quantity, BigDecimal price, String genre, String publishHouse, String language, SellerDetails seller) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.imagePath = imagePath;
        this.format = format;
        this.rate = rate;
        this.quantity = quantity;
        this.price = price;
        this.genre = genre;
        this.publishHouse = publishHouse;
        this.language = language;
        this.seller = seller;
    }
}
