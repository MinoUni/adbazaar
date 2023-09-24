package com.adbazaar.dto.product;

import com.adbazaar.model.AppUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerDetails {

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    private String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("creation_date")
    private LocalDate creationDate;

    public static SellerDetails build(AppUser user) {
        return SellerDetails.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .creationDate(user.getCreationDate())
                .build();
    }
}
