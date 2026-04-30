package com.minhnhat.demo.common.security;

import com.minhnhat.demo.features.auth.service.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private final String SECRET_KEY = "secret-key-demo-secret-key-demo-secret-key-demo";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Access Token expiry: 1 hour
    private static final long ACCESS_TOKEN_EXPIRY = 3600000;
    // Refresh Token expiry: 7 days
    private static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60 * 1000L;

    public String generateAccessToken(UserPrincipal principal) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_EXPIRY);

        return Jwts.builder()
                .setSubject(principal.getUserId())
                .claim("email", principal.getEmail())
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    public String generateAccessToken(String userId, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_EXPIRY);

        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserPrincipal principal) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_EXPIRY);

        return Jwts.builder()
                .setSubject(principal.getUserId())
                .claim("email", principal.getEmail())
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String userId, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_EXPIRY);

        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    // Keep the old method for backward compatibility
    public String generateToken(UserPrincipal principal) {
        return generateAccessToken(principal);
    }

    public boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).get("email", String.class);
    }

    public String getTokenType(String token) {
        return getClaimsFromToken(token).get("type", String.class);
    }

    public boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    private Claims getClaimsFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
