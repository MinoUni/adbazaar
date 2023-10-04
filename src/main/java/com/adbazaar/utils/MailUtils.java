package com.adbazaar.utils;

import com.adbazaar.model.AppUser;
import com.adbazaar.model.VerificationCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Service
public class MailUtils {

    private final JavaMailSender javaMailSender;

    @Value("spring.mail.username")
    private String appEmail;

    public void sendEmail(AppUser user, VerificationCode code, String subject) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(appEmail, "AdBazaar");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            message.setContent(buildContent(user, code), "text/html; charset=UTF-8");
            javaMailSender.send(message);
        } catch (MessagingException | MailException | UnsupportedEncodingException e) {
            throw new MailSendException(e.getMessage());
        }
    }

    private String buildContent(AppUser user, VerificationCode code) {
        return String.format("Dear %s!<br>There is your account verification code:<br><h3>%s</h3><br>", user.getFullName(), code.getCode());
    }
}
