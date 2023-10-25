package com.adbazaar.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserSocialsRepository {

    private final Map<String, List<String>> usersSocials = new ConcurrentHashMap<>();

    public void save(String email, List<String> socials) {
        usersSocials.put(email, socials);
    }

    public List<String> findByEmail(String email) {
        var socials  = Optional.ofNullable(usersSocials.get(email));
        return socials.orElse(List.of());
    }
}
