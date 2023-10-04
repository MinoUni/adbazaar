package com.adbazaar.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewComment {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("book_id")
    private Long bookId;

    private Integer rate;

    @NotBlank(message = "Field {message} must not be blank")
    private String message;
}
