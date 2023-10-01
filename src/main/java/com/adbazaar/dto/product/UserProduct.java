package com.adbazaar.dto.product;

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
public class UserProduct {

    private Long id;

    private String author;

    private String title;

    @JsonProperty("image_path")
    private String imagePath;

    private BigDecimal rate;

    private BigDecimal price;
}
