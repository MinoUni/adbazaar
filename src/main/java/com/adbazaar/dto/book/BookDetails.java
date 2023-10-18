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

}
