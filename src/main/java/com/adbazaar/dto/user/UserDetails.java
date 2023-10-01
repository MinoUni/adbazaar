package com.adbazaar.dto.user;

import com.adbazaar.dto.comment.UserComment;
import com.adbazaar.dto.product.UserProduct;
import com.adbazaar.model.AppUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {

    private Long id;

    @JsonProperty("user_verified")
    private Boolean isVerified;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    private String phone;

    @JsonProperty("birt_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Builder.Default
    private List<String> orders = new ArrayList<>();

    @Builder.Default
    private List<String> favorites = new ArrayList<>();

    @Builder.Default
    private List<UserComment> comments = new ArrayList<>();

    @Builder.Default
    private List<UserProduct> products = new ArrayList<>();

    public static UserDetails build(AppUser user, List<UserProduct> products, List<UserComment> comments) {
        return UserDetails.builder()
                .id(user.getId())
                .isVerified(user.getIsVerified())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .comments(comments)
                .products(products)
                .build();
    }
}