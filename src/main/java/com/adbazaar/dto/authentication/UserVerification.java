package com.adbazaar.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVerification {

    @Schema(type = "string", example = "mark.javar@gmail.com")
    @NotBlank(message = "Field {email} must not be blank")
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
            message = "Invalid email")
    private String email;

    @Size(min = 4, max = 4, message = "Provided not a 4-digit code")
    @NotBlank(message = "Field {verification_code} must not be blank")
    @JsonProperty("verification_code")
    private String verificationCode;
}
