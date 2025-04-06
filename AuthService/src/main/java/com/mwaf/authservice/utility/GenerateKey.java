package com.mwaf.authservice.utility;


import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;

import java.security.Key;

public class GenerateKey {
    public static void main(String[] args) {
        // Generate a secure key for HS512
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        // Encode the key to Base64 so you can store it in your properties file
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("Generated JWT Secret Key: " + base64Key);
    }
}