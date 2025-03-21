package com.wms.authService.config;

import com.wms.authService.entity.RefreshToken;
import com.wms.authService.repository.AuthRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.SecurityException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final AuthRepository authRepository;
    private Key key;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuer("wms-auth-service")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String createRefreshToken(String email) {
        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        authRepository.save(new RefreshToken(email, refreshToken));

        return refreshToken;
    }

    public boolean validateRefreshToken(String email, String refreshToken) {
        Optional<RefreshToken> storedToken = authRepository.findByEmail(email);
        return storedToken.map(token -> token.getRefreshToken().equals(refreshToken)).orElse(false);
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public String validateTokenAndGetEmail(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject(); // ✅ 이메일 반환
        } catch (ExpiredJwtException e) {
            System.out.println("토큰이 만료되었습니다.");
            return "EXPIRED";  // ✅ 만료된 토큰
        } catch (MalformedJwtException e) {
            System.out.println("손상된 토큰입니다.");
            return "INVALID";
        } catch (SecurityException e) {
            System.out.println("서명이 잘못되었습니다.");
            return "INVALID";
        } catch (Exception e) {
            System.out.println("토큰 검증 실패");
            return "INVALID";
        }
    }
}
