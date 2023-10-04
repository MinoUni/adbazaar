package com.adbazaar.repository;

import com.adbazaar.model.VerificationCode;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserVerifyTokenRepository {

    private final Map<String, VerificationCode> usersVerifyCodes = new ConcurrentHashMap<>();

    public VerificationCode save(String email, VerificationCode code) {
        usersVerifyCodes.put(email, code);
        return code;
    }

    public VerificationCode findByEmail(String email) {
        return usersVerifyCodes.get(email);
    }

    public void delete(String email) {
        usersVerifyCodes.remove(email);
    }
}
