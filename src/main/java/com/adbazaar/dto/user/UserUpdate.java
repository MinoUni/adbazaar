package com.adbazaar.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("image_url")
    private String imageUrl;

    @Past
    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^\\([0-9]{3}\\) [0-9]{3}-[0-9]{4}$",
            message = "invalid phone number")
    private String phone;

    private List<String> socials;
}
