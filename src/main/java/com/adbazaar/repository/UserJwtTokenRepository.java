package com.adbazaar.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserJwtTokenRepository {

    private final Map<String, String> userRefreshTokens = new ConcurrentHashMap<>();

    public String save(String email, String token) {
        userRefreshTokens.put(email, token);
        return token;
    }

    public Optional<String> findByEmail(String email) {
        return Optional.ofNullable(userRefreshTokens.get(email));
    }

    public void delete(String email) {
        userRefreshTokens.remove(email);
    }
}
