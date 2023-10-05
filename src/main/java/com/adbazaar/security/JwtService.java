package com.adbazaar.security;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.authentication.RefreshTokenRequest;
import com.adbazaar.dto.authentication.RefreshTokenResp;
import com.adbazaar.exception.JwtTokenException;
import com.adbazaar.exception.RefreshTokenException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.model.AppUser;
import com.adbazaar.repository.UserJwtTokenRepository;
import com.adbazaar.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
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
import static com.adbazaar.utils.MessageUtils.USER_NOT_FOUND_BY_EMAIL;

@RequiredArgsConstructor
@Service
public class JwtService {

    @Value("${application.security.jwt.access.secret-key}")
    private String accessSecretKey;

    @Value("${application.security.jwt.access.expiration-minutes}")
    private Integer accessExpirationMinutes;

    @Value("${application.security.jwt.refresh.secret-key}")
    private String refreshSecretKey;

    @Value("${application.security.jwt.refresh.expiration-days}")
    private Integer refreshExpirationDays;

    private final UserJwtTokenRepository tokenRepo;

    private final UserRepository userRepo;

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

    public RefreshTokenResp refreshTokens(RefreshTokenRequest refreshToken) {
        final var email = extractUserEmailFromRefreshToken(refreshToken.getRefreshToken());
        validateRefreshToken(refreshToken.getRefreshToken(), email);
        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL, email)));
        var newAccessToken = generateAccessToken(user);
        var newRefreshToken = generateRefreshToken(user);
        tokenRepo.save(user.getEmail(), newRefreshToken);
        return RefreshTokenResp.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public ApiResp revokeRefreshToken(String refreshToken) {
        final var email = extractUserEmailFromRefreshToken(refreshToken);
        validateRefreshToken(refreshToken, email);
        tokenRepo.delete(email);
        return ApiResp.builder()
                .status(HttpStatus.OK.value())
                .message(String.format(REFRESH_TOKEN_REVOKED, email))
                .build();
    }

    private void validateRefreshToken(String refreshToken, String email) {
        var token = tokenRepo.findByEmail(email)
                .orElseThrow(() -> new RefreshTokenException(String.format(REFRESH_TOKEN_NOT_FOUND, email)));
        if (!isRefreshTokenValid(refreshToken, token)) {
            throw new RefreshTokenException(REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }
    }

    private Claims extractClaims(String token, String secret) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey(secret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
                 SignatureException | IllegalArgumentException e) {
            throw new JwtTokenException(e.getMessage());
        }
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
