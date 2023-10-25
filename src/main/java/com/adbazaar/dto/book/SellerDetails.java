package com.adbazaar.dto.book;

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

    private Long id;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    private String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonProperty("creation_date")
    private LocalDate creationDate;

}
