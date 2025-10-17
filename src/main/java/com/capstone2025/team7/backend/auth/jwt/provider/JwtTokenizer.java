package com.capstone2025.team7.backend.auth.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenizer {
    @Getter
    @Value("${jwt.key}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    public String encodedBase64SecretKey(String secretKey){
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Map<String, Object> claims,
                                      String subject,
                                      Date expiration,
                                      String base64EncodedSecretKey){
        // 반환 타입을 SecretKey로 명확하게 받음
        SecretKey key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        return Jwts.builder()
                .claims(claims) // setClaims 대신 claims() 사용 권장
                .subject(subject)
                .issuedAt(Calendar.getInstance().getTime())
                .expiration(expiration) // signWith 앞에 두는 것을 권장
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String subject,
                                       Date expiration,
                                       String base64EncodedSecretKey){
        // 반환 타입을 SecretKey로 명확하게 받음
        SecretKey key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(Calendar.getInstance().getTime())
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public Jws<Claims> getClaims(String jws, String baseEncodedSecretKey){
        SecretKey key = getKeyFromBase64EncodedKey(baseEncodedSecretKey);
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jws);

        return claims;
    }

    /**
     * 서명을 검증하는 메서드 (수정됨)
     * Deprecated된 setSigningKey, parseClaimsJws 대신 최신 API 사용
     */
    public void verifySignature(String jws, String baseEncodedSecretKey){
        SecretKey key = getKeyFromBase64EncodedKey(baseEncodedSecretKey);

        Jwts.parser()
                .verifyWith(key) // setSigningKey(key) 대신 사용
                .build()
                .parseSignedClaims(jws); // parseClaimsJws(jws) 대신 사용
    }

    public Date getTokenExpiration(int expirationMinutes){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationMinutes);

        return calendar.getTime();
    }

    /**
     * 키 생성 메서드 (수정됨)
     * 반환 타입을 Key 대신 더 명확한 SecretKey로 변경
     */
    private SecretKey getKeyFromBase64EncodedKey(String base64EncodedSecretKey){
        byte[] keyBytes = Base64.getDecoder().decode(base64EncodedSecretKey.getBytes(StandardCharsets.UTF_8));
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
