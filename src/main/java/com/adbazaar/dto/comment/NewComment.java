package com.adbazaar.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewComment {

    private Double rate;

    @NotBlank(message = "Field {message} must not be blank")
    private String message;
}
