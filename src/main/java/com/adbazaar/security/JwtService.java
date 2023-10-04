package com.adbazaar.security;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.exception.RefreshTokenException;
import com.adbazaar.model.AppUser;
import com.adbazaar.repository.UserJwtTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import static com.adbazaar.utils.MessageUtils.REFRESH_TOKEN_INVALID_OR_EXPIRED;
import static com.adbazaar.utils.MessageUtils.REFRESH_TOKEN_NOT_FOUND;
import static com.adbazaar.utils.MessageUtils.REFRESH_TOKEN_REVOKED;

@Service
public class JwtService {

    private final String accessSecretKey;

    private final Integer accessExpirationMinutes;

    private final String refreshSecretKey;

    private final Integer refreshExpirationDays;

    private final UserJwtTokenRepository tokenRepo;

    public JwtService(@Value("${application.security.jwt.access.secret-key}") String accessSecretKey,
                      @Value("${application.security.jwt.access.expiration-minutes}") Integer accessExpirationMinutes,
                      @Value("${application.security.jwt.refresh.secret-key}") String refreshSecretKey,
                      @Value("${application.security.jwt.refresh.expiration-days}") Integer refreshExpirationDays,
                      UserJwtTokenRepository tokenRepo) {
        this.accessSecretKey = accessSecretKey;
        this.accessExpirationMinutes = accessExpirationMinutes;
        this.refreshSecretKey = refreshSecretKey;
        this.refreshExpirationDays = refreshExpirationDays;
        this.tokenRepo = tokenRepo;
    }

    public String generateAccessToken(UserDetails user) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MINUTE, accessExpirationMinutes);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(calendar.getTime())
                .signWith(getSignInKey(accessSecretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(AppUser user) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.DAY_OF_WEEK, refreshExpirationDays);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(calendar.getTime())
                .signWith(getSignInKey(refreshSecretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsernameFromAccessToken(token);
        return username.equals(userDetails.getUsername()) && isTokenExpired(token, accessSecretKey);
    }

    public boolean isRefreshTokenValid(String refreshToken, String token) {
        return refreshToken.equals(token) && isTokenExpired(refreshToken, refreshSecretKey);
    }

    public String extractUsernameFromAccessToken(String token) {
        return extractClaim(token, accessSecretKey, Claims::getSubject);
    }

    public String extractUserEmailFromRefreshToken(String token) {
        return extractClaim(token, refreshSecretKey, Claims::getSubject);
    }

    public <T> T extractClaim(String token, String secret, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token, secret);
        return claimsResolver.apply(claims);
    }

    public String assignRefreshToken(AppUser user) {
        return tokenRepo.save(user.getEmail(), generateRefreshToken(user));
    }

    public ApiResp revokeRefreshToken(String refreshToken) {
        final var email = extractUserEmailFromRefreshToken(refreshToken);
        var token = tokenRepo.findByEmail(email)
                .orElseThrow(() -> new RefreshTokenException(String.format(REFRESH_TOKEN_NOT_FOUND, email)));
        if (!isRefreshTokenValid(refreshToken, token)) {
            throw new RefreshTokenException(REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }
        tokenRepo.delete(email);
        return ApiResp.builder()
                .status(HttpStatus.OK.value())
                .message(String.format(REFRESH_TOKEN_REVOKED, email))
                .build();
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
        return !extractClaim(token, secret, Claims::getExpiration).before(new Date());
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
