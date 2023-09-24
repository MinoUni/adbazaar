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
@NoArgsConstructor
@AllArgsConstructor
public class ProductShortDetails {

    private Long id;

    @JsonProperty("image_path")
    private String imagePath;

    private String title;

    private String author;

    private String format;

    private BigDecimal rate;

    private Integer quantity;

    private BigDecimal price;

    public static ProductShortDetails build(Product product) {
        return ProductShortDetails.builder()
                .id(product.getId())
                .imagePath(product.getImagePath())
                .title(product.getTitle())
                .author(product.getAuthor())
                .format(product.getFormat())
                .rate(product.getRate())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .build();
    }
}
