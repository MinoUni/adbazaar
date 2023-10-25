package com.adbazaar.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdate {

    @NotBlank(message = "Field {full_name} must not be blank")
    @JsonProperty("full_name")
    private String fullName;

    @NotBlank(message = "Field {image_url} must not be blank")
    @JsonProperty("image_url")
    private String imageUrl;

    @Past
    @NotBlank(message = "Field {birth_date} must not be blank")
    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Field {phone} must not be blank")
    @Pattern(regexp = "^\\([0-9]{3}\\) [0-9]{3}-[0-9]{4}$",
            message = "invalid phone number")
    private String phone;

    @NotNull(message = "Field {socials} must not be null")
    private List<String> socials;
}
