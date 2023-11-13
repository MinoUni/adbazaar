package com.adbazaar.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewBook {

    @NotBlank(message = "Field {title} must not be blank")
    private String title;

    @NotBlank(message = "Field {author} must not be blank")
    private String author;

    @NotBlank(message = "Field {description} must not be blank")
    private String description;

    @NotBlank(message = "Field {format} must not be blank")
    private String format;

    @Positive
    @NotNull(message = "Field {price} can't be null")
    private BigDecimal price;

    @NotBlank(message = "Field {genre} must not be blank")
    private String genre;

    @NotBlank(message = "Field {language} must not be blank")
    private String language;

    @NotBlank(message = "Field {publishing_house} must not be blank")
    @JsonProperty("publishing_house")
    private String publishHouse;

    @Positive
    @NotNull(message = "Field {quantity} can't be null")
    private Integer quantity;
}
