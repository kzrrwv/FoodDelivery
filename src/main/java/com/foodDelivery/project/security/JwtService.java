package com.foodDelivery.project.security;

import com.foodDelivery.project.domen.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    public String generateToken(UserDetails userDetails){
        HashMap<String, Object> claims = new HashMap<>();

        if(userDetails instanceof User customUser){
            claims.put("id", customUser.getId());
            claims.put("email", customUser.getEmail());
            claims.put("role", customUser.getRole());
        }

        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails){
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] decode = Decoders.BASE64.decode(jwtSigningKey);

        return Keys.hmacShaKeyFor(decode);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    //дописать
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims payload = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(payload);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
}
