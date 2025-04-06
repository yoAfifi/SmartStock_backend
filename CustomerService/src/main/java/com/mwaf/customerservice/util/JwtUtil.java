package com.mwaf.customerservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String base64Secret;

    private Key getSigningKey() {
        byte[] decodedKey = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserIdFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.get("roles", List.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    public Long getCustomerIdFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.get("customerId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }
}
