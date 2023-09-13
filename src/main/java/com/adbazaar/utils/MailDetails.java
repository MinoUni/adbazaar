package com.adbazaar.utils;

import com.adbazaar.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailDetails {

    private String subject;
    private String content;
    private AppUser user;
}
