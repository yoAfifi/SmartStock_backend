package com.mwaf.orderservice.util; // or com.mwaf.authservice.util, depending on your project structure

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String base64Secret;

    private final long expiration = 86400000;


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

    public Long getCustomerIdFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.get("customerId", Long.class);
        } catch (Exception e) {
            return null;
        }
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
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostConstruct
    public void debugSecret() {
        System.out.println("JwtUtil loaded base64Secret: " + base64Secret);
    }
}
