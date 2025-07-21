package com.ecommerceapp.dubizlelike.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-millis}")
    private Long accessTokenExpirationMillis;

    @Value("${jwt.refresh-token-expiration-millis}")
    private Long refreshTokenExpirationMillis;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(String userEmail) {
        return generateToken(new HashMap<>(), userEmail, accessTokenExpirationMillis);
    }

    public String generateAccessToken(String userEmail, Map<String, Object> extraClaims) {
        return generateToken(extraClaims, userEmail, accessTokenExpirationMillis);
    }

    public String generateRefreshToken(String userEmail) {
        return generateToken(new HashMap<>(), userEmail, refreshTokenExpirationMillis);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            String userEmail,
            Long expirationMillis
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userEmail)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean isTokenValid(String token, String userEmail) {
        final String username = extractUsername(token);
        return (username.equals(userEmail) && !isTokenExpired(token));
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
