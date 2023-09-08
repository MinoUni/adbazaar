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

    private final String ACCESS_SECRET_KEY;
    private final Integer ACCESS_EXPIRATION_MINUTES;
    private final String REFRESH_SECRET_KEY;
    private final Integer REFRESH_EXPIRATION_DAYS;

    public JwtService(@Value("${application.security.jwt.access.secret-key}") String ACCESS_SECRET_KEY,
                      @Value("${application.security.jwt.access.expiration-minutes}") Integer ACCESS_EXPIRATION_MINUTES,
                      @Value("${application.security.jwt.refresh.secret-key}") String REFRESH_SECRET_KEY,
                      @Value("${application.security.jwt.refresh.expiration-days}") Integer REFRESH_EXPIRATION_DAYS) {
        this.ACCESS_SECRET_KEY = ACCESS_SECRET_KEY;
        this.ACCESS_EXPIRATION_MINUTES = ACCESS_EXPIRATION_MINUTES;
        this.REFRESH_SECRET_KEY = REFRESH_SECRET_KEY;
        this.REFRESH_EXPIRATION_DAYS = REFRESH_EXPIRATION_DAYS;
    }

    public String generateAccessToken(String email) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MINUTE, ACCESS_EXPIRATION_MINUTES);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(calendar.getTime())
                .signWith(getSignInKey(ACCESS_SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsernameFromAccessToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, ACCESS_SECRET_KEY);
    }

    public String extractUsernameFromAccessToken(String token) {
        return extractClaim(token, ACCESS_SECRET_KEY, Claims::getSubject);
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
