package com.adbazaar.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBookOrders {

    private Long id;

    private String author;

    private String title;

    @JsonProperty("image_path")
    private String imagePath;

    private Double rate;

    private BigDecimal price;

    private Integer quantity;
}
