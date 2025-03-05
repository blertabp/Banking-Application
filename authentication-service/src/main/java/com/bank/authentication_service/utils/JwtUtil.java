package com.bank.authentication_service.utils;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.private-key}")
    private Resource privateKeyResource;


    @Value("${jwt.expiration}")
    private long expirationTime;

    private PrivateKey privateKey;


    @EventListener(ContextRefreshedEvent.class)
    public void init() throws Exception {
        this.privateKey = loadPrivateKey();
    }

    private PrivateKey loadPrivateKey() throws Exception {
        byte[] keyBytes;
        try (InputStream is = privateKeyResource.getInputStream()) {
            keyBytes = is.readAllBytes();
        }

        String keyContent = new String(keyBytes);
        keyContent = keyContent.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(keyContent);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }



    public String generateToken(Long userId,String username,String role) {
        return Jwts.builder()
                .subject(username)
                .claims(Map.of("role", role))
                .claims(Map.of("userId", userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }
}
