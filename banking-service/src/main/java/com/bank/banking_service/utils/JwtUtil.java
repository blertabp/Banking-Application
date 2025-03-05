package com.bank.banking_service.utils;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtUtil {

    @Value("${jwt.public-key}")
    private Resource publicKeyResource;


    private PublicKey publicKey;


    @EventListener(ContextRefreshedEvent.class)
    public void init() throws Exception {
        this.publicKey = loadPublicKey();
    }


    private PublicKey loadPublicKey() throws Exception {
        byte[] keyBytes;
        try (InputStream is = publicKeyResource.getInputStream()) {
            keyBytes = is.readAllBytes();
        }

        String keyContent = new String(keyBytes);
        keyContent = keyContent.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(keyContent);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }


    /**
     * Extract username from JWT token
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


    /**
     * Extract userId from JWT token
     */
    public Long extractUserId(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Long.class);
    }


    /**
     * Extract role from JWT token
     */
    public String extractRole(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }


    /**
     * Extract expiration date from JWT token
     */
    public Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }


    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        return  extractExpiration(token).after(new Date());
    }

}
