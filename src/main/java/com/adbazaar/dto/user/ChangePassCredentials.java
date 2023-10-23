package com.adbazaar.dto.user;

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
public class ChangePassCredentials {

    @Schema(type = "string", example = "J@v@rd1kk")
    @NotBlank(message = "Field {cur_pass} must not be blank")
    @Size(min = 8, max = 20, message = "{cur_pass} must be from 8 to 20 symbols length")
    @Pattern(regexp = "^(?=.*[a-z][^А-Яа-яЇїІіЄєҐґЁё]+$)(?=.*[A-Z])(?=.*[a-zA-Z]).*$",
            message = "{curr_pass} must be only with latin letters and contain at least one uppercase and lowercase letter")
    @JsonProperty("cur_pass")
    private String curPassword;

    @Schema(type = "string", example = "J@v@rd1kk")
    @NotBlank(message = "Field {new_pass} must not be blank")
    @Size(min = 8, max = 20, message = "{new_pass} must be from 8 to 20 symbols length")
    @Pattern(regexp = "^(?=.*[a-z][^А-Яа-яЇїІіЄєҐґЁё]+$)(?=.*[A-Z])(?=.*[a-zA-Z]).*$",
            message = "{new_pass} must be only with latin letters and contain at least one uppercase and lowercase letter")
    @JsonProperty("new_pass")
    private String newPassword;
}
