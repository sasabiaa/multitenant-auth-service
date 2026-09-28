package org.project.multitenantauthservice.service.implementation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.project.multitenantauthservice.entity.User;
import org.project.multitenantauthservice.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.token}")
    private String secretToken;


    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15;
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secretToken.getBytes());
    }

    public String generateToken(User user){
        Map<String, Object> claims = new HashMap<>();

        claims.put("user_id", user.getId());
        claims.put("plan", user.getPlan());
        claims.put("plan_status", user.getPlanStatus() != null ? user.getPlanStatus().name() : "ACTIVE");

        if (user.getRole() != null) {
            claims.put("authorities", user.getRole().name());
        }

        return buildToken(claims, user.getUsername(), ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user.getUsername(), REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token){
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username){
        String tokenUsername = extractUsername(token);

        return tokenUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
