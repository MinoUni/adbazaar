package com.adbazaar.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String accessSecretKey;
    private final Integer accessExpirationMinutes;
    private final String refreshSecretKey;
    private final Integer refreshExpirationDays;

    public JwtService(@Value("${application.security.jwt.access.secret-key}") String accessSecretKey,
                      @Value("${application.security.jwt.access.expiration-minutes}") Integer accessExpirationMinutes,
                      @Value("${application.security.jwt.refresh.secret-key}") String refreshSecretKey,
                      @Value("${application.security.jwt.refresh.expiration-days}") Integer refreshExpirationDays) {
        this.accessSecretKey = accessSecretKey;
        this.accessExpirationMinutes = accessExpirationMinutes;
        this.refreshSecretKey = refreshSecretKey;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    public String generateAccessToken(UserDetails userDetails) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MINUTE, accessExpirationMinutes);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(calendar.getTime())
                .signWith(getSignInKey(accessSecretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsernameFromAccessToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, accessSecretKey);
    }

    public String extractUsernameFromAccessToken(String token) {
        return extractClaim(token, accessSecretKey, Claims::getSubject);
    }

    public <T> T extractClaim(String token, String secret, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token, secret);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token, String secret) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token, String secret) {
        return extractClaim(token, secret, Claims::getExpiration).before(new Date());
    }

    private Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private Key getSignInKey(String secret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
