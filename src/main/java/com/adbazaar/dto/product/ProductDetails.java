package com.adbazaar.dto.product;

import com.adbazaar.model.Product;
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
public class ProductDetails {

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

    public static ProductDetails build(Product product) {
        return ProductDetails.builder()
                .id(product.getId())
                .title(product.getTitle())
                .author(product.getAuthor())
                .description(product.getDescription())
                .imagePath(product.getImagePath())
                .format(product.getFormat())
                .price(product.getPrice())
                .rate(product.getRate())
                .quantity(product.getQuantity())
                .genre(product.getGenre())
                .publishHouse(product.getPublishHouse())
                .language(product.getLanguage())
                .seller(SellerDetails.build(product.getSeller()))
                .build();
    }
}
