package com.adbazaar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "Field {full_name} must not be blank")
    @JsonProperty("full_name")
    private String fullName;

    @NotBlank(message = "Field {email} must not be blank")
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
            message = "Invalid email")
    private String email;

    @NotBlank(message = "Field {password} must not be blank")
    @Size(min = 8, max = 20, message = "Password must be from 8 to 20 symbols length")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).*$",
            message = "Password must contain at least one uppercase and lowercase letter")
    @Pattern(regexp = "^[^А-Яа-яЇїІіЄєҐґЁё]+$",
            message = "Password must contain only latin letters")
    private String password;
}
