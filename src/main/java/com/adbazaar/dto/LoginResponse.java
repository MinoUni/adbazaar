package com.adbazaar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    @JsonProperty("user_role")
    private String role;

    @JsonProperty("access_token")
    private String accessToken;
}
