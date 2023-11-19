package com.example.simplesms.security;

import com.example.simplesms.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class TokenProvider {
    private AppProperties appProperties;

    public TokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String createToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getToken().getAccessTokenExpirationMsec());
        String accessTokenSecret = appProperties.getToken().getAccessTokenSecret();

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(accessTokenSecret), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        String accessTokenSecret = appProperties.getToken().getAccessTokenSecret();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(accessTokenSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            String accessTokenSecret = appProperties.getToken().getAccessTokenSecret();
             return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(accessTokenSecret))
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody().getExpiration().after(new Date());

        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
