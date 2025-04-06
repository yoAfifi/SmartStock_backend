package com.mwaf.authservice.utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String base64Secret;

    // Token validity: 1 day in milliseconds (adjust as needed)
    private final long expiration = 86400000;

    /**
     * This method decodes the Base64 secret and creates a Key for HS512.
     */
    private Key getSigningKey() {
        byte[] decodedKey = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    /**
     * Generates a JWT token with the given subject (userId) and additional claims.
     */
    public String generateToken(String userId, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Generates a JWT token with the user ID as the subject.
     * This method is retained for backward compatibility but does not include additional claims.
     */
    public String generateToken(String userId) {
        return generateToken(userId, Map.of());
    }

    @PostConstruct
    public void debugSecret() {
        System.out.println("JwtUtil loaded base64Secret: " + base64Secret);
    }
}
