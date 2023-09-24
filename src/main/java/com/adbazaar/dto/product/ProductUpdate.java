package com.adbazaar.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdate {

    private String title;
    private String author;
    private String description;
    private String format;
    private BigDecimal rate;
    @JsonProperty("image_path")
    private String imagePath;
    private BigDecimal price;
    private Integer quantity;
    private String genre;
    private String language;
    @JsonProperty("publishing_house")
    private String publishHouse;
}
