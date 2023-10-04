package com.adbazaar.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewBook {

    private String title;

    private String author;

    private String description;

    private String format;

    @JsonProperty("image_path")
    private String imagePath;

    private BigDecimal price;

    private String genre;

    private String language;

    @JsonProperty("publishing_house")
    private String publishHouse;

    private Integer quantity;
}
