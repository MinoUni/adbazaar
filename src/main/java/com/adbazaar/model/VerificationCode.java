package com.adbazaar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Random;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCode {

    private String userEmail;

    private String code;

    @Builder.Default
    private LocalDateTime expirationDate = LocalDateTime.now().plusHours(1L);

    public static VerificationCode build(String email) {
        return VerificationCode.builder()
                .userEmail(email)
                .code(String.format("%04d", new Random().nextInt(10_000)))
                .build();
    }

}
