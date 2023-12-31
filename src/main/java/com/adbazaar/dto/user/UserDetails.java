package com.adbazaar.dto.user;

import com.adbazaar.dto.book.UserBook;
import com.adbazaar.dto.book.UserBookOrders;
import com.adbazaar.dto.comment.UserComment;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {

    private Long id;

    @JsonProperty("verified")
    private Boolean isVerified;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    @JsonProperty("image_url")
    private String imageUrl;

    private String phone;

    @JsonProperty("birt_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    private List<String> socials;

    private Set<UserBookOrders> orders = new HashSet<>();

    private Set<UserBook> favorites = new HashSet<>();

    private List<UserComment> comments = new ArrayList<>();

    private List<UserBook> books = new ArrayList<>();

    public UserDetails(Long id,
                       Boolean isVerified,
                       String fullName,
                       String email,
                       String phone,
                       String imageUrl,
                       LocalDate dateOfBirth) {
        this.id = id;
        this.isVerified = isVerified;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.dateOfBirth = dateOfBirth;
    }
}
