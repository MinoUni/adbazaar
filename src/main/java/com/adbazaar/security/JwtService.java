package com.adbazaar.security;

import com.adbazaar.exception.RefreshTokenException;
import com.adbazaar.model.AppUser;
import com.adbazaar.model.Token;
import com.adbazaar.repository.TokenRepository;
import com.adbazaar.repository.UserRepository;
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
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

    private final String accessSecretKey;
    private final Integer accessExpirationMinutes;
    private final String refreshSecretKey;
    private final Integer refreshExpirationDays;
    private final TokenRepository tokenRepo;
    private final UserRepository userRepo;

    public JwtService(@Value("${application.security.jwt.access.secret-key}") String accessSecretKey,
                      @Value("${application.security.jwt.access.expiration-minutes}") Integer accessExpirationMinutes,
                      @Value("${application.security.jwt.refresh.secret-key}") String refreshSecretKey,
                      @Value("${application.security.jwt.refresh.expiration-days}") Integer refreshExpirationDays,
                      TokenRepository tokenRepo,
                      UserRepository userRepo) {
        this.accessSecretKey = accessSecretKey;
        this.accessExpirationMinutes = accessExpirationMinutes;
        this.refreshSecretKey = refreshSecretKey;
        this.refreshExpirationDays = refreshExpirationDays;
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
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
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(calendar.getTime())
                .signWith(getSignInKey(refreshSecretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsernameFromAccessToken(token);
        return username.equals(userDetails.getUsername()) && isTokenExpired(token, accessSecretKey);
    }

    public boolean isRefreshTokenValid(String refreshToken, Token token) {
        var userId = extractUserIdFromRefreshToken(refreshToken);
        return Objects.equals(userId, token.getId()) && isTokenExpired(refreshToken, refreshSecretKey);
    }

    public String extractUsernameFromAccessToken(String token) {
        return extractClaim(token, accessSecretKey, Claims::getSubject);
    }

    public Long extractUserIdFromRefreshToken(String token) {
        return Long.valueOf(extractClaim(token, refreshSecretKey, Claims::getSubject));
    }

    public <T> T extractClaim(String token, String secret, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token, secret);
        return claimsResolver.apply(claims);
    }

    public String assignRefreshToken(AppUser user) {
        var token = generateRefreshToken(user);
        if (user.getToken() == null) {
            user.setToken(Token.builder().user(user).token(token).build());
            userRepo.save(user);
            return token;
        }
        user.getToken().setToken(token);
        userRepo.save(user);
        return token;
    }

    public void revokeRefreshToken(String refreshToken) {
        final var userId = extractUserIdFromRefreshToken(refreshToken);
        var token = tokenRepo.findById(userId)
                .orElseThrow(() -> new RefreshTokenException(String.format("Token with id %d not found", userId)));
        if (!isRefreshTokenValid(refreshToken, token)) {
            throw new RefreshTokenException("Refresh token is invalid or has been expired");
        }
        token.revoke();
        tokenRepo.delete(token);
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
