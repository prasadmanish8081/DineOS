package com.dineos.util;

import com.dineos.config.JwtProperties;
import com.dineos.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtTokenUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        return generateToken(user, getExpirationFromNow());
    }

    public String generateToken(User user, Instant expiresAt) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        return extractUsername(token).equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenInvalid(String token) {
        try {
            return isTokenExpired(token);
        } catch (Exception ex) {
            return true;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public Instant getExpirationFromNow() {
        return Instant.now().plus(jwtProperties.getExpirationMs(), ChronoUnit.MILLIS);
    }
}
